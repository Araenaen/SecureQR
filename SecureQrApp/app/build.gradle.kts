import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    try {
        localProperties.load(localPropertiesFile.inputStream())
        println("INFO: local.properties cargado correctamente.")
    } catch (e: Exception) {
        println("ERROR: No se pudo cargar local.properties: ${e.message}")
    }
} else {
    println("ADVERTENCIA: No se encontró el archivo local.properties. La API Key podría estar vacía.")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.secureqr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.secureqr"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // SEGURIDAD: Inyección de la API Key en tiempo de compilación.
        // Esto evita hardcodear credenciales en el control de versiones.
        // La propiedad GSB_API_KEY debe residir en el archivo local.properties del desarrollador.
        buildConfigField(
            "String",
            "GSB_API_KEY",
            "\"${localProperties.getProperty("GSB_API_KEY") ?: ""}\""
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            // Ofuscación de código con R8 para dificultar la ingeniería inversa
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true // Habilitación de Jetpack Compose para UI declarativa
        buildConfig = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Núcleo de Android y Compose ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.material)

    // --- Inyección de Dependencias (Hilt) ---
    // Facilita la creación de componentes desacoplados y testables.
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- Persistencia Local (Room) ---
    // Capa de abstracción sobre SQLite para manejo seguro de consultas.
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Extensiones para Coroutines
    kapt("androidx.room:room-compiler:2.6.1")

    // --- Red y Comunicación (Retrofit & OkHttp) ---
    // Cliente HTTP tipo-seguro para consumir la API de Google Safe Browsing.
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Cámara y Procesamiento de Imágenes (CameraX + ZXing) ---
    // CameraX provee abstracción de hardware consistente entre fabricantes.
    // ZXing (Zebra Crossing) realiza la decodificación matricial del QR.
    val camerax_version = "1.3.1"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("com.google.zxing:core:3.5.2")

    // --- Concurrencia (Coroutines) ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // TESTING
    // Añade estas dos líneas para Hilt en tests unitarios locales
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptTest("com.google.dagger:hilt-compiler:2.51.1")

    // Dependencias de testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Ejemplo de test de instrumentación
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Ejemplo de test de instrumentación
//iconos
    implementation("androidx.compose.material:material-icons-extended")
}