import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

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
