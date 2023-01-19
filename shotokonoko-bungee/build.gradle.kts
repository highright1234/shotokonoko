

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}")
    // 한번 써보라는 뜻으로 api 함


    // 코루틴 버전 빨리 안오르기도 하고
    // 새 프로젝트 짤때마다 추가해야하는거 귀찮음
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")


}