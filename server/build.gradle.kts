plugins {
    kotlin("jvm") version "1.5.30"
}

group = "io.github.heineson.kdevlog"
version = "0.1"

var commonsIoVersion = "2.11.0"
var kotlinLoggingVersion = "2.0.11"
var log4jVersion = "2.14.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("commons-io:commons-io:$commonsIoVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")


    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
