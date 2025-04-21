plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ismanieji.parkingosystema"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ismanieji.parkingosystema"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")  // Exclude duplicate DEPENDENCIES files
            excludes.add("META-INF/INDEX.LIST")  // Exclude duplicate INDEX.LIST files
            excludes.add("META-INF/io.netty.versions.properties")  // Exclude duplicate io.netty.versions.properties files
        }
    }
}

dependencies {
    // AndroidX Libraries (Core, UI, Compose, etc.)
    implementation(libs.androidx.core.ktx)  // AndroidX core extensions
    implementation(libs.androidx.lifecycle.runtime.ktx)  // Lifecycle extensions
    implementation(libs.androidx.activity.compose)  // Activity Compose integration
    implementation(platform(libs.androidx.compose.bom))  // BOM for Compose
    implementation(libs.androidx.ui)  // Jetpack Compose UI
    implementation(libs.androidx.ui.graphics)  // Jetpack Compose Graphics
    implementation(libs.androidx.ui.tooling.preview)  // Compose preview
    implementation(libs.androidx.material3)  // Material Design 3
    implementation(libs.firebase.appdistribution.gradle)  // Firebase App Distribution
    testImplementation(libs.junit)  // JUnit for unit tests
    androidTestImplementation(libs.androidx.junit)  // Android JUnit tests
    androidTestImplementation(libs.androidx.espresso.core)  // Espresso UI tests
    androidTestImplementation(platform(libs.androidx.compose.bom))  // Compose BOM for Android Test
    androidTestImplementation(libs.androidx.ui.test.junit4)  // Compose test JUnit integration
    debugImplementation(libs.androidx.ui.tooling)  // Debug tooling for Compose
    debugImplementation(libs.androidx.ui.test.manifest)  // Manifest for Compose test

    implementation("com.squareup.retrofit2:retrofit:2.9.0")  // Retrofit for API calls
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")  // Gson converter for Retrofit
    implementation("org.maplibre.gl:android-sdk:9.5.0") // MapLibre SDK (ensure version exists)
    implementation("com.google.android.material:material:1.7.0") {
        exclude(group = "com.android.support", module = "support-compat")
    }
    implementation("androidx.appcompat:appcompat:1.7.0")  // AppCompat for backward compatibility
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")  // ConstraintLayout for UI design
    implementation("org.osmdroid:osmdroid-android:6.1.16")  // OSMDroid for OpenStreetMap support
}

// Global exclusion for legacy support-compat
configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
}
