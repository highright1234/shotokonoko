package io.github.highright1234.shotokonokodebug

import com.github.shynixn.mccoroutine.bungeecord.SuspendingPlugin
import com.github.shynixn.mccoroutine.bungeecord.launch
import com.outstandingboy.donationalert.entity.Donation
import io.github.highright1234.shotokonoko.collections.newPlayerArrayList
import io.github.highright1234.shotokonoko.listener.ChatScanner
import io.github.highright1234.shotokonoko.listener.events
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import io.github.highright1234.shotokonoko.listener.listen
import io.github.highright1234.shotokonoko.loader.DynamicLoader
import io.github.highright1234.shotokonoko.pluginmessage.MessageChannel
import io.github.highright1234.shotokonoko.pluginmessage.PluginMessageUtil
import io.github.highright1234.shotokonoko.text
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import net.md_5.bungee.api.event.PostLoginEvent

@Suppress("Unused")
class ShotokonokoDebug: SuspendingPlugin() {
    private var donation: Donation? = null
    private val pluginMessageChannel = MessageChannel("shotokonoko-debug:test")
    override suspend fun onEnableAsync() {
        DynamicLoader.load(
            listOf("jitpack" to "https://jitpack.io"),
            listOf("com.github.outstanding1301:donation-alert-api:1.0.0")
        )

        donation = Donation().apply {
            nickName = "하라"
            comment = "바부"
            amount = 1000L
            id = "HighRight"
        }
        logger.info("$donation")

        launchPlayerGCChecker()
        launchPsycho()

        pluginMessageChannel.init()
        PluginMessageUtil.listen(pluginMessageChannel) {
            logger.info("user: ${it.name}, data: ${readUTF()}")
        }
    }

    private fun launchPlayerGCChecker() = launch {
        val arrayList = newPlayerArrayList()
        launch {
            while (true) {
                proxy.broadcast(text(arrayList.joinToString()))
                delay(1000)
            }
        }
        events<PostLoginEvent>().collect {
            arrayList += it.player
        }
    }

    private fun launchPsycho() = launch {

        val event = withTimeoutOrNull(60000L) { listen<PostLoginEvent>() } ?: return@launch
        val player = event.player
        player.sendMessage(text("채팅 아무거나 써보셈, 번지임"))

        ChatScanner(player).await() // 다음 채팅에서는 singing 관련해서 오류나니 참고
            .onSuccess {
                player.sendMessage(text(it + "라고 보냈구만"))
            }.onFailure {
                val message = when (it) {
                    is PlayerQuitException -> "플레이어 쉐키가 나감"
                    is TimeoutCancellationException -> "1분 지났다"
                    else -> "버근가"
                }
                player.sendMessage(text(message))
            }
    }
}