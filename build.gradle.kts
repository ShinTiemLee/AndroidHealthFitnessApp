buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        // Add the Google Services classpath for Firebase
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("com.google.gms:google-services:4.3.15") // Firebase classpath



    }
}
allprojects {
    repositories {

    }
}
