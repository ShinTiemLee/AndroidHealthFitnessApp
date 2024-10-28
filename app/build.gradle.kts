plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Apply Google Services plugin


}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shin.hfapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        namespace = "com.shin.hfapp"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding= true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.1.0")) // Firebase BoM (Bill of Materials)
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation ("com.github.lecho:hellocharts-library:1.5.8")
    implementation(libs.firebase.firestore)
    androidTestImplementation(libs.junit.junit)


}
