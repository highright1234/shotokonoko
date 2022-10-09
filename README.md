# Shotokonoko

[![Maven Central](https://img.shields.io/maven-central/v/io.github.highright1234/shotokonoko)](https://search.maven.org/artifact/io.github.highright1234/shotokonoko)

### Utilities for Paper Kotlin plugin

~~이름은 이렇지만 당신도 쓰게될거야~~

예제:   
- [shotokonoko-debug](https://github.com/highright1234/shotokonoko/tree/main/shotokonoko-debug/src/main/java/io/github/highright1234/shotokonokodebug)      
- [Wiki](https://github.com/highright1234/shotokonoko/wiki)

### 사용법
build.gradle.kts
```kts
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.github.highright1234:shotokonoko:VERSION")
}
```
plugin.yml
```yaml
libraries:
  - io.github.highright1234:shotokonoko:VERSION
```