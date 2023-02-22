import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

plugins {
    kotlin("jvm") version Versions.KOTLIN
    id("org.jetbrains.dokka") version Versions.KOTLIN
    kotlin("plugin.serialization") version Versions.KOTLIN
}


group = "io.github.highright1234"
version = "0.1.4"

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
    if ("debug" in project.name) {
        tasks.register<Jar>("pluginsUpdate") {
            var pluginName = rootProject.name.split("-").joinToString(separator = "") { it.capitalizeAsciiOnly() }
            if ("debug" in project.name) pluginName += "Debug"
            archiveBaseName.set(pluginName)
            from(sourceSets["main"].output)
            from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
            val serverDir = File(rootProject.rootDir, ".server")
            doLast {
                // 내 마크 서버 환경 불러오기
                val serverFolder = File("E:\\.bungee\\")
                if (!serverDir.exists() && serverFolder.exists()) {
                    serverDir.mkdir()
                    copy {
                        from(serverFolder)
                        include("**/**")
                        into(serverDir)
                    }
                }
                val bukkits = serverDir
                    .listFiles()!!
                    .filter { "server" in it.name }
                val proxy = File(serverDir, "proxy")

                val pluginsFolders: List<File> =
                    if ("bukkit" in project.name) {
                        bukkits.map { File(it, "plugins") }
                    } else if ("bungee" in project.name) {
                        File(proxy, "plugins").let(::listOf)
                    } else {
                        return@doLast
                    }

                pluginsFolders.forEach {
                    copy {
                        from(archiveFile)
                        if (File(it, archiveFileName.get()).exists()) {
                            File(it, archiveFileName.get()).delete()
                        }
                        into(it)
                    }
                }
                pluginsFolders.forEach {
                    // auto-reloader
                    val updateFolder = File(it, "update")
                    if (!updateFolder.exists()) return@doLast
                    File(updateFolder, "RELOAD").delete()
                }
            }
        }
        tasks.named("build") { finalizedBy("pluginsUpdate") }
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