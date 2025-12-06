package com.secureqr.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Horizontal
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler // Necesario para abrir el navegador
import androidx.compose.ui.text.style.TextDecoration // Para el subrayado
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()

    val uriHandler = LocalUriHandler.current

    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay historial de escaneos.")
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(history) { scan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = scan.url,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF0000EE),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                try {
                                    uriHandler.openUri(scan.url)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = scan.riskLevel.name,
                                color = scan.riskLevel.toColor(),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            val date = Date(scan.scanTimestamp)
                            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            Text(
                                text = format.format(date),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteScan(scan) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar escaneo",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                }
            }
        }
    }
}