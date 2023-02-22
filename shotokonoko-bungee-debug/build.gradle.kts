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