# Shotokonoko

[![Maven Central](https://img.shields.io/maven-central/v/io.github.highright1234/shotokonoko)](https://search.maven.org/artifact/io.github.highright1234/shotokonoko)

### Utilities for Paper Kotlin plugin

~~이름은 이렇지만 당신도 쓰게될거야~~

특징:
- monun님의 라이브러리들의 DSL 빌더의 실행부분을 suspend 형식 지원
- kommand 느낌의 papi expansion dsl builder
- MutableDelay( 한글로 뭐라고 할지 모르겠음 )
- 스토리지 기능
- 채팅 스캐너
- 코틀린을 위한 리스닝 최적화
- 플레이어 데이터 자동제거 collection

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






대충 코드
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
val cooldownData = CoolDownAttribute<UUID>(5000L)

...

val event = listen<PlayerJoinEvent> { it.player.name == "HighRight" }
val player = event.player
// 쿨다운중 아니면 아래 코드들 실행함
cooldownData.withCoolDown(player.uniqueId)
val storage = withContext(plugin.asyncDispatcher) {
    getDataStore("${player.uniqueId}")
}
player.sendMessage("비밀 이야기 해봐")
// 10초 지나면 밑에 코드들 작동 안함
withSafeTimeout(10000L) {
    player.health = 0.0
    player.sendMessage("주거 임마")
}
val chat = ChatScanner(player).await().getOrThrow().string // Component to String
Bukkit.broadcast(text("<${player.name}> $chat"))
storage.set("secret", chat)
events<EntityDamageEvent>().filter { it.entity == player }.collect {
    event.player.sendMessage("허접♥")
    
}
```
