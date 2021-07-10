plugins {
    kotlin("jvm") version "1.5.20"
}

group = "io.github.heineson.kdevlog"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
}
