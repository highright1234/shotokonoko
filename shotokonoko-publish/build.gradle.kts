plugins {
    id("org.jetbrains.dokka") version Versions.KOTLIN
    `maven-publish`
    signing
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
            name = "server"
            url = rootProject.uri(".server/libraries")
        }
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
        fun MavenPublication.setup(target: Project) {
            artifactId = target.name
            from(project.components["java"])
            artifact(project.tasks["sourcesJar"])
            artifact(project.tasks["javadocJar"])

            pom {
                name.set(target.name)
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
        val bukkit = project(":${rootProject.name}-bukkit")
        val bungee = project(":${rootProject.name}-bungee")
        create<MavenPublication>("bukkit") {
            setup(bukkit)
        }
        create<MavenPublication>("bungee") {
            setup(bungee)
        }
    }
}

signing {
    isRequired = true
    sign(publishing.publications["bukkit"], publishing.publications["bungee"])
}
