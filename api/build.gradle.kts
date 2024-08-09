plugins {
    id("java")
}

group = "com.ultreon.mods.devices"
version = "0.9.0"

repositories {
    mavenCentral()

    maven("https://github.com/Ultreon/ultreon-data/raw/main/.mvnrepo")
    maven("https://github.com/Ultreon/corelibs/raw/main/.mvnrepo")
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")

    implementation("io.github.ultreon:ubo:1.3.0")
    implementation("io.github.ultreon.corelibs:commons-v0:0.2.0")
}

tasks.test {
    useJUnitPlatform()
}