package io.github.highright1234.shotokonoko.listener

import com.github.shynixn.mccoroutine.bungeecord.bungeeCordDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.coroutine.withTimeoutResult
import kotlinx.coroutines.withContext
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent

class ChatScanner(private val player: ProxiedPlayer, private val timeOut: Long = 60000L) {
    suspend fun await() : Result<String> {
        var result: Result<String>
        withContext(plugin.bungeeCordDispatcher) {
            result = withTimeoutResult(timeOut) {
                player.listen<ChatEvent>().onSuccess { it.isCancelled = true }.map { it.message }
            // 이거 throw해서 오류나는게 아니라 result로 변환됨
            }.mapCatching { it.getOrThrow() }
        }
        return result
    }
}