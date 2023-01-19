

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
}