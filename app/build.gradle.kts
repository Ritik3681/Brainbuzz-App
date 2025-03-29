plugins {
  //  alias(libs.plugins.android.application)
   alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.android.application")
    id("com.google.firebase.crashlytics")


}

android {
    namespace = "com.example.aptitude"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aptitude"
        minSdk = 26
        targetSdk = 34
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    viewBinding {
        enable = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/LICENSE")
        exclude ("META-INF/LICENSE.txt")
        exclude ("META-INF/license.txt")
        exclude ("META-INF/NOTICE")
        exclude ("META-INF/NOTICE.txt")
        exclude ("META-INF/notice.txt")
        exclude ("META-INF/ASL2.0")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database.ktx)
   //testImplementation(libs.junit)
    //androidTestImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-database:20.0.3")
   implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(libs.androidx.material3.android)
    //testImplementation ("junit:junit:4.13.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")


    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    implementation ("com.airbnb.android:lottie:6.0.0")










}
