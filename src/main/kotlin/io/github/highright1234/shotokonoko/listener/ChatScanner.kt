package io.github.highright1234.shotokonoko.listener

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.coroutine.withSafeTimeout
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ChatScanner(private val player: Player, private val timeOut: Long = 60000L) {
    suspend fun await() : Result<Component> {
        var result: Result<Component>
        withContext(plugin.asyncDispatcher) {
            result = withSafeTimeout(timeOut) {
                player.listen<AsyncChatEvent>().onSuccess { it.isCancelled = true }.map { it.message() }
            }.mapCatching { it.getOrThrow() }
        }
        return result
    }
}