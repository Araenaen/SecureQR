package com.secureqr.repository

import android.util.Log
import com.secureqr.data.local.ScanDao
import com.secureqr.data.local.ScanEntity
import com.secureqr.data.remote.ClientInfo
import com.secureqr.data.remote.GsbApiService
import com.secureqr.data.remote.GsbRequest
import com.secureqr.data.remote.ThreatEntry
import com.secureqr.data.remote.ThreatInfo
import com.secureqr.domain.model.QrRiskLevel
import com.secureqr.util.Constants
import com.secureqr.util.HashUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScanRepository @Inject constructor(
    private val apiService: GsbApiService,
    private val scanDao: ScanDao
) {
    val allScans: Flow<List<ScanEntity>> = scanDao.getAllScans()
    private val cache = mutableMapOf<String, QrRiskLevel>()

    suspend fun validateUrl(url: String): QrRiskLevel = withContext(Dispatchers.IO) {
        val cachedResult = cache[url]

        // --- LÓGICA DE CACHÉ CORREGIDA ---
        if (cachedResult != null && cachedResult != QrRiskLevel.DESCONOCIDO) {
            Log.d("ScanRepository", "Cache Hit: $cachedResult para la URL: $url")
            // Devolvemos el resultado de la caché sin tocar la BD ni la red.
            return@withContext cachedResult
        }

        Log.d("ScanRepository", "Cache Miss o reintento. Llamando a la API para la URL: $url")

        // --- LÓGICA DE LLAMADA A LA API Y BASE DE DATOS ---
        val request = buildGsbRequest(url)

        try {
            val response = apiService.checkUrl(request = request)

            val riskLevel = if (response.isSuccessful) {
                val matches = response.body()?.matches
                if (matches.isNullOrEmpty()) {
                    Log.i("ScanRepository", "URL '${url}' es segura según GSB.")
                    QrRiskLevel.SEGURO // La API respondió, pero no encontró amenazas.
                } else {
                    Log.w("ScanRepository", "URL '${url}' es MALICIOSA según GSB.")
                    QrRiskLevel.MALICIOSO // La API encontró amenazas.
                }
            } else {
                // Si la API falla (error 4xx, 5xx), se considera DESCONOCIDO.
                Log.e(
                    "ScanRepository",
                    "Error de la API: ${response.code()} - ${response.message()}"
                )
                QrRiskLevel.DESCONOCIDO
            }

            cache[url] = riskLevel

            val urlHash = HashUtils.sha256(url)
            val newScan = ScanEntity(
                url = url,
                urlHash = urlHash,
                riskLevel = riskLevel,
                scanTimestamp = System.currentTimeMillis(),
                lastValidationTimestamp = System.currentTimeMillis()
            )
            scanDao.insertScan(newScan)

            // Devolvemos el nivel de riesgo determinado.
            return@withContext riskLevel

        } catch (e: Exception) {
            Log.e("ScanRepository", "Excepción de red al validar URL: ${e.message}", e)

            cache[url] = QrRiskLevel.DESCONOCIDO

            return@withContext QrRiskLevel.DESCONOCIDO
        }
    }

    private fun buildGsbRequest(url: String): GsbRequest {
        return GsbRequest(
            client = ClientInfo(),
            threatInfo = ThreatInfo(
                threatTypes = listOf(
                    "MALWARE",
                    "SOCIAL_ENGINEERING",
                    "UNWANTED_SOFTWARE",
                    "POTENTIALLY_HARMFUL_APPLICATION"
                ),
                platformTypes = listOf("ANY_PLATFORM"),
                threatEntryTypes = listOf("URL"),
                threatEntries = listOf(ThreatEntry(url))
            )
        )
    }

    suspend fun deleteScan(scan: ScanEntity) = scanDao.deleteScan(scan)
}