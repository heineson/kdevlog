plugins {
    kotlin("jvm") version "1.5.21"
}

group = "io.github.heineson.kdevlog"
version = "0.1"

var arrowVersion = "0.13.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
