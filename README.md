# Shotokonoko

[![Maven Central](https://img.shields.io/maven-central/v/io.github.highright1234/shotokonoko-bukkit)](https://search.maven.org/artifact/io.github.highright1234/shotokonoko-bukkit)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.highright1234/shotokonoko-bungee)](https://search.maven.org/artifact/io.github.highright1234/shotokonoko-bungee)

### Utilities for Paper Kotlin plugin

~~이름은 이렇지만 당신도 쓰게될거야~~

특징:
- monun님의 라이브러리들의 DSL 빌더의 실행부분을 suspend 형식 지원
- kommand 느낌의 papi expansion dsl builder
- MutableDelay( 한글로 뭐라고 할지 모르겠음 )
- 스토리지 기능  
  - Json, MongoDB 지원
- 채팅 스캐너
- 코틀린을 위한 리스닝 최적화
- 플레이어 데이터 자동제거 collection
- DynamicLoader(central 이외의 RemoteRepository서버 이용가능)
- BungeeUtil
- PluginMessageUtil
- 번지코드 config api

TODO:
- storage 업그레이드
- MutableDelay 최적화
- 버킷의 문제인지 라이브러리의 문제인진 모르겠지만  
  1틱정도 후에 리스너가 실행되는것같음 

예제:   
- [shotokonoko-debug](https://github.com/highright1234/shotokonoko/tree/main/shotokonoko-bukkit-debug/src/main/java/io/github/highright1234/shotokonokodebug)      
- [Wiki](https://github.com/highright1234/shotokonoko/wiki)

### 사용법
build.gradle.kts
```kts
kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
    mavenCentral()
}

dependencies {
    // for bukkit
    compileOnly("io.github.highright1234:shotokonoko-bukkit:VERSION")
    // for bungee
    compileOnly("io.github.highright1234:shotokonoko-bungee:VERSION")
}
```


plugin.yml
```yaml
libraries:
  - io.github.highright1234:shotokonoko-bukkit:VERSION
```

bungee.yml
```yaml
libraries:
  - io.github.highright1234:shotokonoko-bungee:VERSION
```





대충 버킷 코드
```kt
kommand {
  register("command") {
    suspendingExecutes {
      ...
    }
  }
}

papi {
  expansion("placeholder") {
    argument("value") {
      executes {
        val value: String by arguments
        value
      }
    }
  }
}

val players = newPlayerArrayList() // it removes a player who exit
val cooldownData = CooldownAttribute<UUID>(5000L)

...

val event = listen<PlayerJoinEvent> { it.player.name == "HighRight" }
val player = event.player
// 쿨다운중 아니면 아래 코드들 실행함
cooldownData.withCoolDown(player.uniqueId) {
  return
}
val storage = withContext(plugin.asyncDispatcher) {
    getDataStore("${player.uniqueId}")
}
player.sendMessage("비밀 이야기 해봐")
// 10초 지나면 밑에 코드들 작동 안함
ChatScanner(player).await().onSuccess { component ->
  val chat = component.string // Component to String
  Bukkit.broadcast(text("<${player.name}> $chat"))
  storage.set("secret", chat)
  events<EntityDamageEvent>().filter { it.entity == player }.collect {
    event.player.sendMessage("허접♥")    
  }
}.onFailture { throwable ->
  when (throwable) {
    is TimeoutCancellationException {
      ...
    }
    is PlayerQuitException {
      ...
    }
  }
}
```
