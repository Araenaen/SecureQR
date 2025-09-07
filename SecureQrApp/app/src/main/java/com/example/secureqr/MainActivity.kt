package com.example.secureqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Icons for navigation and other UI elements
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbIncandescent
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.ArrowBack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRScannerApp()
        }
    }
}

// Data classes for the history and education sections
data class HistoryItem(val id: Int, val type: String, val title: String, val date: String, val content: String)
data class EducationItem(val title: String, val content: String? = null, val items: List<EducationSubItem>? = null)
data class EducationSubItem(val icon: @Composable () -> Unit, val text: String, val description: String)

val historyData = listOf(
    HistoryItem(1, "Website", "Example Website", "Hoy, 2:30 PM", "https://www.example.com"),
    HistoryItem(2, "WiFi", "MyNetwork", "Ayer, 5:45 PM", "SSID: MyNetwork, Pass: secret"),
    HistoryItem(3, "Text", "Hello World! This is a test...", "Dic 2, 3:15 PM", "Hello World! This is a test message with some content."),
    HistoryItem(4, "Contact", "John Doe", "Nov 30, 11:20 AM", "Name: John Doe, Phone: 123-456-7890"),
)

val educationData = listOf(
    EducationItem(
        title = "¿Qué son los códigos QR?",
        content = "Los códigos QR (Quick Response) son códigos de barras cuadrados que pueden almacenar varios tipos de información. Se pueden escanear rápidamente con la cámara de su teléfono inteligente para acceder a sitios web, conectarse a WiFi, guardar información de contacto y mucho más."
    ),
    EducationItem(
        title = "Tipos de códigos QR",
        items = listOf(
            EducationSubItem(icon = { Icon(Icons.Default.Public, contentDescription = null) }, text = "URL de sitio web", description = "Enlaces a sitios web, páginas de destino o contenido en línea."),
            EducationSubItem(icon = { Icon(Icons.Default.Wifi, contentDescription = null) }, text = "Red WiFi", description = "Se conecta a redes inalámbricas automáticamente."),
            EducationSubItem(icon = { Icon(Icons.Default.ContactMail, contentDescription = null) }, text = "Información de contacto", description = "Guarda los datos de contacto en su agenda."),
            EducationSubItem(icon = { Icon(Icons.Default.Sms, contentDescription = null) }, text = "Mensaje de texto", description = "Texto plano, mensajes o información.")
        )
    )
)

@Composable
fun QRScannerApp() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFF3F4F6)
        ) {
            when (selectedTab) {
                0 -> ScannerScreen()
                1 -> HistoryScreen()
                2 -> EducationScreen()
            }
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "QR Scanner",
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "QR Scanner", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF111827),
        contentColor = Color(0xFFD1D5DB)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Escáner") },
            label = { Text("Escáner") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFFD1D5DB),
                unselectedTextColor = Color(0xFFD1D5DB),
                indicatorColor = Color(0xFF374151)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
            label = { Text("Historial") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFFD1D5DB),
                unselectedTextColor = Color(0xFFD1D5DB),
                indicatorColor = Color(0xFF374151)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.School, contentDescription = "Educación") },
            label = { Text("Educación") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFFD1D5DB),
                unselectedTextColor = Color(0xFFD1D5DB),
                indicatorColor = Color(0xFF374151)
            )
        )
    }
}

@Composable
fun ScannerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // QR Scanner Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF9FAFB))
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Camera Icon",
                tint = Color(0xFF6B7280),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Posiciona el código QR en el marco",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "La cámara detectará y escaneará el código automáticamente",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Flash button
            Button(
                onClick = { /* Handle Flashlight action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.WbIncandescent, contentDescription = "Linterna", tint = Color(0xFF4B5563))
                Spacer(Modifier.width(8.dp))
                Text("Linterna", color = Color(0xFF4B5563))
            }
            Spacer(Modifier.width(16.dp))
            // Scan button
            Button(
                onClick = { /* Handle Scan action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR")
                Spacer(Modifier.width(8.dp))
                Text("Escanear QR")
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Escaneo",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tus códigos QR escaneados recientemente",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(Modifier.height(16.dp))
        historyData.forEach { item ->
            HistoryCard(item)
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFD1D5DB)),
                contentAlignment = Alignment.Center
            ) {
                when (item.type) {
                    "Website" -> Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF4B5563))
                    "WiFi" -> Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF4B5563))
                    "Text" -> Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF4B5563))
                    "Contact" -> Icon(Icons.Default.ContactMail, contentDescription = null, tint = Color(0xFF4B5563))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.type, fontSize = 12.sp, color = Color(0xFF6B7280))
                Text(text = item.title, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
                Text(text = item.date, fontSize = 14.sp, color = Color(0xFF6B7280))
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Handle Copy action */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copiar", fontSize = 12.sp, color = Color(0xFF4B5563))
                    }
                    Button(
                        onClick = { /* Handle Open action */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Abrir", fontSize = 12.sp, color = Color(0xFF3B82F6))
                    }
                    Button(
                        onClick = { /* Handle Delete action */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar", fontSize = 12.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}

@Composable
fun EducationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Acerca de los Códigos QR",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Aprende sobre los códigos QR y consejos de escaneo",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(Modifier.height(16.dp))
        EducationCard(educationData[0])
        Spacer(Modifier.height(16.dp))
        EducationCard(educationData[1])
    }
}

@Composable
fun EducationCard(item: EducationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            if (item.content != null) {
                Text(text = item.content, fontSize = 14.sp, color = Color(0xFF4B5563))
            }
            if (item.items != null) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    item.items.forEach { subItem ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            subItem.icon()
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(text = subItem.text, fontWeight = FontWeight.SemiBold)
                                Text(text = subItem.description, fontSize = 12.sp, color = Color(0xFF6B7280))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QRScannerApp()
}