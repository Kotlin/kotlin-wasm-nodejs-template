
@file:OptIn(ExperimentalWasmDsl::class)

package wasmtime

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmWasiTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.wasm.runtime.dsl.runtime
import org.tukaani.xz.XZInputStream
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension

fun KotlinWasmWasiTargetDsl.wasmtime(version: String) {
    runtime(
        "wasmtime",
        version
    ) {
        val tmpFile = (this@wasmtime as KotlinJsIrTarget)
            .project
            .layout
            .buildDirectory
            .file("tmp/wasmtime/wasmtime.tar")

        download { os: String, arch: String, version: String ->
            val artifactName = artifactFileName(os, arch, version)

            "https://github.com/bytecodealliance/wasmtime/releases/download/$version/$artifactName"
        }

        archiveOperation { archiveOperation, entry ->
            if (entry.extension == "zip") {
                archiveOperation.zipTree(entry)
            } else {
                val newFile = tmpFile
                    .get()
                    .asFile

                xzToTar(entry.toFile(), newFile)
                archiveOperation.tarTree(newFile)
            }
        }

        executable { os: String, arch: String, version: String, installationDir: Path? ->
            val executableFile = if (os.lowercase().contains("windows")) {
                "wasmtime.exe"
            } else {
                "wasmtime"
            }

            installationDir!!
                .resolve(artifactName(os, arch, version))
                .resolve(executableFile)
                .absolutePathString()
        }

        runArgs { _: Path, entry: Path ->
            val newArgs = mutableListOf<String>()

            newArgs.add("-W")
            newArgs.add("function-references,gc,exceptions,stack-switching")


            newArgs.add("--invoke")
            newArgs.add("_start")

            newArgs.add(entry.normalize().absolutePathString())

            newArgs
        }

        testArgs { _: Path, entry: Path ->
            val newArgs = mutableListOf<String>()

            newArgs.add("-W")
            newArgs.add("function-references,gc,exceptions,stack-switching")


            newArgs.add("--invoke")
            newArgs.add("startUnitTests")

            newArgs.add(entry.normalize().absolutePathString())

            newArgs
        }
    }
}


private fun xzToTar(xzFile: File, tarFile: File) {
    tarFile.parentFile.mkdirs()
    XZInputStream(xzFile.inputStream().buffered()).use { xzIn ->
        tarFile.outputStream().buffered().use { out ->
            xzIn.copyTo(out)
        }
    }
}

private fun artifactFileName(os: String, arch: String, version: String): String {
    return artifactName(os, arch, version) + if (os.lowercase().contains("windows")) {
        ".zip"
    } else {
        ".tar.xz"
    }
}

private fun artifactName(os: String, arch: String, version: String): String {
    val wasmtimeSuffix = when {
        os.lowercase().contains("linux") -> when {
            arch.lowercase().contains("x86_64") || arch.lowercase().contains("amd64") ->
                "x86_64-linux"

            arch.lowercase().contains("aarch64") || arch.lowercase().contains("aarch64") ->
                "aarch64-linux"

            else -> error("unsupported arch type $os, $arch")
        }

        os.lowercase().contains("mac") -> when {
            arch.lowercase().contains("x86_64") || arch.lowercase().contains("amd64") ->
                "x86_64-macos"

            arch.lowercase().contains("aarch64") || arch.lowercase().contains("aarch64") ->
                "aarch64-macos"

            else -> error("unsupported arch type $os, $arch")
        }

        os.lowercase().contains("windows") -> "x86_64-windows"
        else -> error("unsupported os type $os, $arch")
    }

    return "wasmtime-$version-$wasmtimeSuffix"
}
