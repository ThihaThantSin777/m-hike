plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.mhike.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mhike.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "OWM_API_KEY", "\"394dbc95b0a0ff411b957beb76f7eb3e\"")
        buildConfigField("String", "OWM_BASE_URL", "\"https://api.openweathermap.org/\"")

    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // No composeOptions{} in Kotlin 2.x

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }

    packaging {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

dependencies {
    // --- Compose BOM ---
    implementation(platform(libs.androidx.compose.bom.v20240902))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.compose.v193)
    implementation(libs.androidx.navigation.compose)

    // --- Lifecycle (for viewModelScope) ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // --- Hilt ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // --- Room (KTX + KSP compiler) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // --- KotlinX DateTime ---
    implementation(libs.kotlinx.datetime)

    // --- Logging ---
    implementation(libs.timber)

    // --- Coil (only if you added media/photos) ---
    implementation(libs.coil.compose)


    implementation(libs.accompanist.permissions.v0250)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)

    // JSON
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Image loading for weather icons
    implementation(libs.coil.compose)
}

kapt { correctErrorTypes = true }
