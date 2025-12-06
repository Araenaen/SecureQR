package com.secureqr.ui.education

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
// CAMBIO: Se eliminan las importaciones de 'automirrored'
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.HelpOutline // Se usa la versión estándar
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Subject // Se usa la versión estándar
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val WarningColor = Color(0xFFFFA000)
val SuccessColor = Color(0xFF388E3C)

@Composable
fun EducationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Mantente un Paso Adelante del Fraude",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // --- Tarjeta: ¿Qué es el Quishing? ---
        EducationCard(
            icon = Icons.Default.HelpOutline, // CAMBIO: Usamos el icono estándar
            title = "¿Qué es el Quishing?"
        ) {
            Text(
                text = "El \"Quishing\" (QR Phishing) es un ciberataque donde los estafadores usan códigos QR para llevarte a sitios web falsos. Su objetivo es robar tu información personal, contraseñas o datos bancarios.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // --- Tarjeta: Señales de Alerta ---
        EducationCard(
            icon = Icons.Default.WarningAmber,
            title = "Señales de Alerta",
            iconTint = WarningColor
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoPoint(
                    icon = Icons.Default.Subject, // CAMBIO: Usamos el icono estándar
                    text = "Contexto sospechoso: ¿El QR está en un lugar inesperado o pegado sobre otro?"
                )
                InfoPoint(
                    icon = Icons.Default.Public,
                    text = "URLs extrañas o acortadas que aparecen después de escanear."
                )
                InfoPoint(
                    icon = Icons.Default.WarningAmber,
                    text = "Páginas que piden información personal o credenciales de forma inmediata."
                )
            }
        }

        // --- Tarjeta: Cómo Protegerte ---
        EducationCard(
            icon = Icons.Default.GppGood,
            title = "Cómo Protegerte",
            iconTint = SuccessColor
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoPoint(
                    icon = Icons.Default.CheckCircleOutline,
                    text = "Utiliza siempre SecureQR para analizar los enlaces antes de abrirlos."
                )
                InfoPoint(
                    icon = Icons.Default.CheckCircleOutline,
                    text = "Desconfía de los QR en lugares públicos. Verifica siempre su autenticidad."
                )
                InfoPoint(
                    icon = Icons.Default.CheckCircleOutline,
                    text = "Nunca introduzcas contraseñas o datos bancarios en un sitio al que llegaste desde un QR sin estar 100% seguro."
                )
            }
        }

        Text(
            text = "Tu primera línea de defensa es la precaución. ¡Escanea de forma inteligente!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun EducationCard(
    icon: ImageVector,
    title: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
fun InfoPoint(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}


