plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version Versions.KOTLIN
    `maven-publish`
    signing
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

group = "io.github.highright1234"
version = "0.0.4"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("io.github.monun:tap-api:${Versions.TAP}")
    compileOnly("io.github.monun:invfx-api:${Versions.INVFX}")
    compileOnly("io.github.monun:kommand-api:${Versions.KOMMAND}")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:${Versions.MC_COROUTINE}")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:${Versions.MC_COROUTINE}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")
        from("$buildDir/dokka/html")
    }
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "MavenCentral"

            credentials {
                val ossrhUsername: String by project
                val ossrhPassword: String by project
                username = ossrhUsername
                password = ossrhPassword
            }

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            artifactId = project.name
            from(project.components["java"])
            artifact(project.tasks["sourcesJar"])
            artifact(project.tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Kotlin util for Paper plugin")
                url.set("https://github.com/highright1234/${rootProject.name}")
                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }

                developers {
                    developer {
                        id.set("highright1234")
                        name.set("HighRight")
                        email.set("highright1234@gmail.com")
                        url.set("https://github.com/highright1234")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/highright1234/${rootProject.name}.git")
                    developerConnection.set("scm:git:ssh://github.com:highright1234/${rootProject.name}.git")
                    url.set("https://github.com/highright1234/${rootProject.name}")
                }
            }
        }
    }
}

signing {
    isRequired = true
    sign(publishing.publications)
}

tasks.register<Jar>("libraryUpdate") {
    archiveBaseName.set(project.name)
    from(sourceSets["main"].output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    val serverDir = File(rootDir, ".server")
    val plugins = File(serverDir, "plugins")
    val libraries = File(serverDir, "libraries")

    var directoryFolder = libraries

    "io.github.highright1234.shotokonoko".split(".").plus("${project.version}").forEach {
        directoryFolder = File(directoryFolder, it)
    }

    doLast {
        // 내 마크 서버 환경 불러오기
        val serverFolder = File("E:\\.server\\")
        if (!serverDir.exists() && serverFolder.exists()) {
            serverDir.mkdir()
            copy {
                from(serverFolder)
                include("**/**")
                into(serverDir)
            }
        }
        // 플러그인 적용
        if (!directoryFolder.exists()) libraries.mkdirs()

        copy {
            from(archiveFile)
            if (File(directoryFolder, archiveFileName.get()).exists()) {
                File(directoryFolder, archiveFileName.get()).delete()
            }
            into(directoryFolder)
        }
        // auto-reloader
        val updateFolder = File(plugins, "update")
        if (!updateFolder.exists()) return@doLast
        File(updateFolder, "RELOAD").delete()
    }
}

tasks.named("build") { finalizedBy("libraryUpdate") }

allprojects {
    if (hasProperty("buildScan")) {
        extensions.findByName("buildScan")?.withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
}