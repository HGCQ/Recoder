plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "yuhan.hgcq.client"
    compileSdk = 34


    defaultConfig {
        applicationId = "yuhan.hgcq.client"
        minSdk = 26
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

    /* Retrofit */
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    /* Okhttp */
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.github.franmontiel:PersistentCookieJar:v1.0.1")

    /* Metadata */
    implementation ("com.drewnoakes:metadata-extractor:2.19.0")

    /* RecyclerView */
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    /* Glide */
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    /* Material !*/
    implementation ("com.google.android.material:material:1.10.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /* Room DB */
    implementation ("androidx.room:room-runtime:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.5.0")
    testImplementation ("androidx.room:room-testing:2.5.0")

    /* JUnit */
    testImplementation ("org.robolectric:robolectric:4.10.2")
    testImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("androidx.test:core:1.5.0")
    testImplementation ("junit:junit:4.13.2")

    /* Glide */
    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")

    /* Dialog */
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")
}
