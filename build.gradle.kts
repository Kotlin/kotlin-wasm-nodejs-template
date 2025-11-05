@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    wasmJs {
        binaries.executable()
        nodejs()
    }
}
