package com.secureqr.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.secureqr.domain.model.QrRiskLevel
import com.secureqr.util.ImageAnalyzer
import java.util.concurrent.Executors


@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.scanState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Usamos un Box principal para apilar la cámara, el overlay y el diálogo
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. VISTA DE LA CÁMARA A PANTALLA COMPLETA
        if (state is ScanUiState.Scanning) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        // Es importante que el modifier de Compose maneje el fillMaxSize
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            Executors.newSingleThreadExecutor(),
                            ImageAnalyzer { url ->
                                viewModel.onQrCodeDetected(url)
                            }
                        )

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) { /* Añadir logging si es necesario */ }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                // MODIFICADOR CLAVE: Ocupa toda la pantalla (como estaba originalmente)
                modifier = Modifier.fillMaxSize()
            )

            // 2. OVERLAY DEL MARCO DE REFERENCIA (con transparencia)
            CameraOverlayFrame(modifier = Modifier.fillMaxSize())

        }

        // 3. Manejo de estados de UI (flota encima de todo)
        when (val s = state) {
            is ScanUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ScanUiState.Result -> {
                ScanResultDialog(
                    url = s.url,
                    risk = s.risk,
                    onDismiss = { viewModel.resetScan() }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun CameraOverlayFrame(modifier: Modifier = Modifier) {
    // Definimos el color oscuro semitransparente para el fondo
    val scrimColor = Color(0x60000000) // 38% de opacidad

    // Define el radio de la esquina para el redondeo (ajusta este valor en .dp)
    val cornerRadius = 24.dp

    Canvas(modifier = modifier.fillMaxSize()) {
        // Obtenemos las dimensiones de la pantalla
        val screenWidth = size.width
        val screenHeight = size.height

        // Definimos el tamaño y posición del "agujero" (el marco de referencia)
        val frameWidth = screenWidth * 0.8f
        val frameHeight = frameWidth

        val frameTop = (screenHeight - frameHeight) / 2f
        val frameLeft = (screenWidth - frameWidth) / 2f

        val cornerRadiusPx = cornerRadius.toPx()

        // 1. Dibuja el fondo oscuro semitransparente
        drawRect(color = scrimColor)

        // 2. Dibuja el "agujero" transparente con ESQUINAS REDONDEADAS
        drawRoundRect(
            color = Color.Transparent,
            topLeft = androidx.compose.ui.geometry.Offset(frameLeft, frameTop),
            size = androidx.compose.ui.geometry.Size(frameWidth, frameHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )

        // 3. Dibuja un BORDE BLANCO de color para que el marco sea visible (también redondeado)
        drawRoundRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(frameLeft, frameTop),
            size = androidx.compose.ui.geometry.Size(frameWidth, frameHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}
@Composable
fun ScanResultDialog(url: String, risk: QrRiskLevel, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Análisis de Seguridad") },
        text = {
            Column {
                Text("URL Detectada:", style = MaterialTheme.typography.labelLarge)
                Text(url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0000EE),
                    textDecoration = TextDecoration.Underline, // Subrayado
                    modifier = Modifier.clickable {
                        // 3. Acción al hacer click: abrir navegador
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            // Manejo de error si la URL no es válida (opcional pero recomendado)
                            e.printStackTrace()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Veredicto:", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = risk.name,
                    color = risk.toColor(),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
                when(risk) {
                    QrRiskLevel.SEGURO -> Text("Sitio no listado en bases de amenazas conocidas.")
                    QrRiskLevel.MALICIOSO -> Text("¡PELIGRO! Sitio reportado como phishing o malware.", color = Color.Red)
                    QrRiskLevel.DESCONOCIDO -> Text("No se pudo verificar (Sin conexión). Proceda con cautela.")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}