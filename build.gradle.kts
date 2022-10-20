plugins {
    kotlin("jvm") version Versions.KOTLIN
    id("org.jetbrains.dokka") version Versions.KOTLIN
    `maven-publish`
    signing
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
}

group = "io.github.highright1234"
version = "0.0.7"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

fun DependencyHandlerScope.monunLibrary(name: String, version: String) {
    compileOnly("io.github.monun:$name-api:$version")

    // 버전 올라갈떄마다 nms 코드 관련 있어서 내가 직접 수정해야함
    // 귀찮아서 지원 안시킴
//    api("io.github.monun:$name-api:$version")
//    runtimeOnly("io.github.monun:$name-core:$version") // LibraryLoader에서 로드해줌
}

dependencies {
    compileOnly("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")
    implementation("net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}")
    monunLibrary("tap", Versions.TAP)
    monunLibrary("invfx", Versions.INVFX)
    monunLibrary("kommand", Versions.KOMMAND)

    // 한번 써보라는 뜻으로 api 함
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:${Versions.MC_COROUTINE}")
    runtimeOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:${Versions.MC_COROUTINE}")

    // 코루틴 버전 빨리 안오르기도 하고
    // 새 프로젝트 짤때마다 추가해야하는거 귀찮음
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}")
    compileOnly("io.papermc.paper:paper-api:${Versions.MINECRAFT}-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
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
    tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
}