rootProject.name = "kotlin-wasm-nodejs-example"

include(":mymodule")

pluginManagement {
    resolutionStrategy {
        repositories {
            gradlePluginPortal()
            mavenLocal()
        }
    }
}
