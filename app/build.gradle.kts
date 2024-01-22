plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.faceanalyzer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.faceanalyzer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

        // ...
        // Use this dependency to bundle the model with your app
        implementation ("com.google.mlkit:face-detection:16.1.5")
        // ...
        // Use this dependency to use the dynamically downloaded model in Google Play Services
        implementation ("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")



    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-common:20.4.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}