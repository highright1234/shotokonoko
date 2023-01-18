import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("com.github.outstanding1301:donation-alert-api:1.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

val directoryName = rootProject.name.replace("-", "") +"debug"
val pluginName = rootProject.name.split("-").joinToString(separator = "") { it.capitalizeAsciiOnly() }
val thisPluginName = pluginName + "Debug"

tasks.register<Jar>("pluginsUpdate") {
    archiveBaseName.set(thisPluginName)
    from(sourceSets["main"].output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    val serverDir = File(rootDir, ".server")
    val plugins = File(serverDir, "plugins")
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
        if (!plugins.exists()) return@doLast
        copy {
            from(archiveFile)
            if (File(plugins, archiveFileName.get()).exists()) {
                File(plugins, archiveFileName.get()).delete()
            }
            into(plugins)
        }
        // auto-reloader
        val updateFolder = File(plugins, "update")
        if (!updateFolder.exists()) return@doLast
        File(updateFolder, "RELOAD").delete()
    }
}

tasks.named("build") { finalizedBy("pluginsUpdate") }