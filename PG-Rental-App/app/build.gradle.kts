plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.roombuddy.pgrentalapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shubham.pgrentalapp2"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    // 🔹 Android core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 🔹 Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 📍 Live location (Google Fused Location)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 🗺️ OSMDroid (OpenStreetMap) – STABLE & FREE
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // 🔹 JSON
    implementation("com.google.code.gson:gson:2.10.1")
}
