plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    val kotlinVersion = libs.versions.kotlin.get()
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.tukaani:xz:1.9")
}
