plugins {
    kotlin("jvm") version Versions.KOTLIN
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    repositories {
        mavenCentral()
    }
}

group = "io.github.highright1234"
version = "0.1.0"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

subprojects {
    if ("publish" in project.name) return@subprojects
    dependencies {
        val platform =
            if ("bukkit" in project.name) "bukkit"
            else if ("bungee" in project.name) "bungeecord"
            else error("unknown: ${project.name}")

        val isDebug = "debug" in project.name

        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
        if (isDebug) {
            // -debug 지우는거
            compileOnly(project(":${project.name}".substring(0, project.name.length - 5)))
            compileOnly(kotlin("stdlib-jdk8"))
            compileOnly(kotlin("reflect"))
        } else {
            api("com.github.shynixn.mccoroutine:mccoroutine-$platform-api:${Versions.MC_COROUTINE}")
            runtimeOnly("com.github.shynixn.mccoroutine:mccoroutine-$platform-core:${Versions.MC_COROUTINE}")
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
        }
        compileOnly(kotlin("stdlib-jdk8"))
        compileOnly(kotlin("reflect"))
        if ("bukkit" in project.name) {
            compileOnly("io.papermc.paper:paper-api:${Versions.MINECRAFT}-R0.1-SNAPSHOT")
        } else if ("bungee" in project.name) {
            compileOnly("io.github.waterfallmc:waterfall-api:${Versions.MINECRAFT}-R0.1-SNAPSHOT")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

allprojects {
    if (hasProperty("buildScan")) {
        extensions.findByName("buildScan")?.withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
    tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
}