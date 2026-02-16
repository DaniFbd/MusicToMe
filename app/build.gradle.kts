plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android") // <-- esto es clave
    kotlin("kapt")
}

android {
    namespace = "com.myown.musictome"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.myown.musictome"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // BOM para Compose (controla versiones automáticamente)
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // Compose Core
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity para Compose
    val activityComposeVersion = "1.8.0"
    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    // Lifecycle + ViewModel
    val lifecycleVersion = "2.6.1"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    // Hilt para ViewModel
    val hiltComposeVersion = "1.1.0"
    val hiltVersion = "2.48"
    implementation("androidx.hilt:hilt-navigation-compose:$hiltComposeVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // DocumentFile (para leer archivos)
    val documentFile = "1.0.1"
    implementation("androidx.documentfile:documentfile:$documentFile")

    // Coil para imágenes
    val coilVersion = "2.4.0"
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // System UI (barra de estado y navegación)
    val systemUiVersion = "0.34.0"
    implementation("com.google.accompanist:accompanist-systemuicontroller:$systemUiVersion")

    // Core KTX
    val coreVersion = "1.10.1"
    implementation("androidx.core:core-ktx:$coreVersion")

    //Coroutines
    val coroutinesVersion = "1.6.0"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Media3 - El estándar moderno para audio en Android
    val media3Version = "1.3.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    // Test
    val junitVersion = "4.13.2"
    val androidTestVersion = "1.1.5"
    val androidTestCore = "3.5.1"
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$androidTestVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$androidTestCore")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Herramientas de depuración
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}
