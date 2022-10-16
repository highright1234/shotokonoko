package io.github.highright1234.shotokonokodebug

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.listener.ChatScanner
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import io.github.highright1234.shotokonoko.listener.exception.TimedOutException
import io.github.highright1234.shotokonoko.listener.listen
import io.github.highright1234.shotokonoko.plus
import io.github.highright1234.shotokonoko.storage.getDataStore
import io.github.highright1234.shotokonoko.storage.getValue
import io.github.highright1234.shotokonoko.withTimeOut
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import kotlinx.coroutines.flow.*
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent

class ShotokonokoDebug: SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        plugin = this
        kommand {
            TestKommand.register(this)
            register("set") {
                requires { isPlayer }

                then("key" to string()) {
                    then("value" to int()) {
                        executes { ctx ->
                            val key: String by ctx
                            val value: Int by ctx
                            val store = getDataStore(player.uniqueId.toString())
                            store.set(key, value)
                        }
                    }
                }
            }
            register("inc") {
                requires { isPlayer }

                then("key" to string()) {
                    executes { ctx ->
                        val key: String by ctx
                        val store = getDataStore(player.uniqueId.toString())
                        store.increment(key, 1)
                    }
                }
            }
            register("get") {
                requires { isPlayer }

                then("key" to string()) {
                    executes { ctx ->
                        val key: String by ctx
                        val store = getDataStore(player.uniqueId.toString())
                        player.sendMessage("${store.get(key, Int::class.java)}")
                    }
                }
            }
            register("getBy") {
                requires { isPlayer }

                executes { ctx ->
                    val store = getDataStore(player.uniqueId.toString())
                    val test: Int? by store
                    player.sendMessage("$test")
                }
            }
            register("save") {
                requires { isPlayer }

                executes { ctx ->
                    val store = getDataStore(player.uniqueId.toString())
                    store.save()
                }
            }

        }
        launch {
            val event = listen<PlayerJoinEvent>()
            event.player.sendMessage("채팅 아무거나 써보셈")
            val timeoutData = withTimeOut(5000L) {
                event.player.sendMessage("5초 지났으니 잘죽엉")
                event.player.health = 0.0
            }
            val chatResult = ChatScanner(event.player).await()
            timeoutData.complete()
            chatResult.onSuccess {
                event.player.sendMessage(it + "라고 보냈구만")
            }.onFailure {
                val message = when (it) {
                    is PlayerQuitException -> "플레이어 쉐키가 나감"
                    is TimedOutException -> "1분 지났다"
                    else -> "버근가"
                }
                event.player.sendMessage(message)
            }
        }
    }
}