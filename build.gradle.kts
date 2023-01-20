plugins {
    kotlin("jvm") version Versions.KOTLIN
    id("org.jetbrains.dokka") version Versions.KOTLIN
}


group = "io.github.highright1234"
version = "0.1.2"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    repositories {
        mavenCentral()
    }

    if ("publish" in project.name) return@subprojects
    dependencies {
        val platform =
            if ("bukkit" in project.name) "bukkit"
            else if ("bungee" in project.name) "bungeecord"
            else null

        val isDebug = "debug" in project.name

        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
        if (isDebug) {
            compileOnly("org.mongodb:mongodb-driver-sync:4.8.2")
            compileOnly(project(":${project.name}".substring(0, project.name.length - 5)))
            compileOnly(kotlin("stdlib-jdk8"))
            compileOnly(kotlin("reflect"))
        } else {
            if ("common" !in project.name) api(project(":shotokonoko-common"))
            platform?.let {
                api("com.github.shynixn.mccoroutine:mccoroutine-$platform-api:${Versions.MC_COROUTINE}")
                runtimeOnly("com.github.shynixn.mccoroutine:mccoroutine-$platform-core:${Versions.MC_COROUTINE}")
            }
            implementation("org.mongodb:mongodb-driver-sync:4.8.2")
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
        }
        compileOnly("org.apache.maven.resolver:maven-resolver-api:1.8.2")
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