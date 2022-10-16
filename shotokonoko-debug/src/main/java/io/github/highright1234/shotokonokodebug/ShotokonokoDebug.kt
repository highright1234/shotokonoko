package io.github.highright1234.shotokonokodebug

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko
import io.github.highright1234.shotokonoko.collections.newPlayerArrayList
import io.github.highright1234.shotokonoko.coroutine.withSafeTimeout
import io.github.highright1234.shotokonoko.listener.ChatScanner
import io.github.highright1234.shotokonoko.listener.events
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import io.github.highright1234.shotokonoko.listener.exception.TimedOutException
import io.github.highright1234.shotokonoko.listener.listen
import io.github.highright1234.shotokonoko.plus
import io.github.monun.kommand.kommand
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.event.player.PlayerJoinEvent

class ShotokonokoDebug: SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        Shotokonoko.register(this)
        kommand {
            TestKommand.register(this)
        }
        launchPlayerGCChecker()
        launchPsycho()
    }

    private fun launchPlayerGCChecker() = launch {
        val arrayList = newPlayerArrayList()
        launch {
            while (true) {
                server.broadcast(Component.text(arrayList.joinToString()))
                delay(1000)
            }
        }
        events<PlayerJoinEvent>().collect {
            arrayList += it.player
        }
    }

    private fun launchPsycho() = launch {
        val event = listen<PlayerJoinEvent>()
        event.player.sendMessage("채팅 아무거나 써보셈")
        val timeoutData = withSafeTimeout(5000L) {
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