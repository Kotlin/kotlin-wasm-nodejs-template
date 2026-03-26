@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    wasmJs {
        binaries.executable()
        nodejs()
    }
    
    sourceSets {
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
