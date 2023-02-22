plugins {
    kotlin("jvm") version "1.7.20" // 없으면 오류남
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}