@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

repositories {
    mavenCentral()
}

// To download a custom version
// plugins.withType<org.jetbrains.kotlin.gradle.targets.js.d8.D8Plugin> {
//     the<org.jetbrains.kotlin.gradle.targets.js.d8.D8EnvSpec>().apply {
//         version = "13.4.61"
//     }
// }

kotlin {
    wasmJs {
        binaries.executable()
        // nodejs()
        d8()
    }
}

// To run a local version
// tasks.withType<org.jetbrains.kotlin.gradle.targets.js.d8.D8Exec>().forEach {
//     it.executable = "/path-to/d8"
//     it.d8Args += listOf("-a", "-b")
// }
