import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Config.Android.compileSdkVersion)
    buildToolsVersion(Config.Android.buildToolsVersion)

    defaultConfig {
        applicationId = "io.sentry.sample"
        minSdkVersion(Config.Android.minSdkVersionDebug)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments = mapOf(
            "clearPackageData" to "true"
        )
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions("version")

    productFlavors {
        create("staging") {
            minSdkVersion(Config.Android.minSdkVersionDebug)
        }
        create("production") {
            minSdkVersion(Config.Android.minSdkVersion)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":sentry-android"))

    implementation(Config.Libs.appCompat)
    implementation(Config.Libs.constraintLayout)
    implementation(Config.Libs.timber)

    testImplementation(kotlin(Config.kotlinStdLib, KotlinCompilerVersion.VERSION))
    testImplementation(Config.TestLibs.junit)
    androidTestImplementation(Config.TestLibs.espressoCore)
    androidTestImplementation(Config.TestLibs.androidxCore)
    androidTestImplementation(Config.TestLibs.androidxRunner)
    androidTestImplementation(Config.TestLibs.androidxJunit)
    androidTestUtil(Config.TestLibs.androidxOrchestrator)
}
