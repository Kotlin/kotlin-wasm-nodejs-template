import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import java.io.OutputStream

plugins {
    kotlin("multiplatform")
}

val kotlin_repo_url: String? = project.properties["kotlin_repo_url"] as String?

repositories {
    mavenCentral()
    kotlin_repo_url?.also { maven(it) }
}

kotlin {
    wasmJs {
        binaries.executable()
        nodejs()
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