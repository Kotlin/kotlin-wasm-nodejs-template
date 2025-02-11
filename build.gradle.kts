import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import java.io.OutputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

val kotlin_repo_url: String? = project.properties["kotlin_repo_url"] as String?
val kotlinLanguageVersionOverride = providers.gradleProperty("kotlin_language_version")
    .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
    .orNull
val kotlinApiVersionOverride = providers.gradleProperty("kotlin_api_version")
    .map(org.jetbrains.kotlin.gradle.dsl.KotlinVersion::fromVersion)
    .orNull
val kotlinAdditionalCliOptions = providers.gradleProperty("kotlin_additional_cli_options")
    .map { it.split(" ") }
    .orNull

repositories {
    mavenCentral()
    kotlin_repo_url?.also { maven(it) }
}

kotlin {
    wasmJs {
        binaries.executable()
        nodejs()

        compilations.configureEach {
            compileTaskProvider.configure {
                if (kotlinLanguageVersionOverride != null) {
                    compilerOptions {
                        languageVersion.set(kotlinLanguageVersionOverride)
                        logger.info("[KUP] ${this@configure.path} : set LV to $kotlinLanguageVersionOverride")
                    }
                }
                if (kotlinApiVersionOverride != null) {
                    compilerOptions {
                        apiVersion.set(kotlinApiVersionOverride)
                        logger.info("[KUP] ${this@configure.path} : set APIV to $kotlinApiVersionOverride")
                    }
                }
                if (kotlinAdditionalCliOptions != null) {
                    compilerOptions {
                        freeCompilerArgs.addAll(kotlinAdditionalCliOptions)
                        logger.info(
                            "[KUP] ${this@configure.path} : added ${
                                kotlinAdditionalCliOptions.joinToString(
                                    " "
                                )
                            }"
                        )
                    }
                }
                compilerOptions {
                    // output reported warnings even in the presence of reported errors
                    freeCompilerArgs.add("-Xreport-all-warnings")
                    logger.info("[KUP] ${this@configure.path} : added -Xreport-all-warnings")
                    // output kotlin.git-searchable names of reported diagnostics
                    freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
                    logger.info("[KUP] ${this@configure.path} : added -Xrender-internal-diagnostic-names")
                    freeCompilerArgs.add("-Wextra")
                    logger.info("[KUP] ${this@configure.path}: added -Wextra")
                }
            }
        }
    }
}

tasks.withType<NodeJsExec>().all {

    val result = StringBuilder()

    standardOutput = object : OutputStream() {
        override fun write(b: Int) {
            result.append(b.toChar())
        }

    }

    doLast {
        println(result.toString())

        val expectedString1 = "Hello from Kotlin/Wasm"

        val lines = result.lines().filter { it.isNotEmpty() }
        check(lines.size == 1) {
            "Expected 1 lines, actual: ${lines.size}"
        }

        check(lines[0] == expectedString1) {
            "Expected '$expectedString1', actual: '${lines[0]}'"
        }
    }
}