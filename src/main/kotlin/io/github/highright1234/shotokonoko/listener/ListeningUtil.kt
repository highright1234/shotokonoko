package io.github.highright1234.shotokonoko.listener

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object ListeningUtil {
    suspend fun <T : Event> listener(
        player: Player,
        clazz: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): Result<T> {
        val exitListener = object: Listener {  }
        val listener = object: Listener {  }
        val completableDeferred = CompletableDeferred<Result<T>>()
        plugin.server.pluginManager.registerEvent(clazz, listener, priority, { _, event ->
            if (!ignoreCancelled || event !is Cancellable|| !event.isCancelled) {
                @Suppress("UNCHECKED_CAST")
                if (filter(event as T) && event.safePlayer == player) {
                    completableDeferred.complete(Result.success(event))
                    HandlerList.unregisterAll(listener)
                    HandlerList.unregisterAll(exitListener)
                }
            }
        }, plugin)
        plugin.server.pluginManager.registerEvent(
            PlayerQuitEvent::class.java,
            exitListener,
            EventPriority.NORMAL,
            { _, event ->
                event as PlayerQuitEvent
                if (player != event.player) return@registerEvent
                completableDeferred.complete(Result.failure(PlayerQuitException()))
                HandlerList.unregisterAll(listener)
                HandlerList.unregisterAll(exitListener)
            }, plugin
        )
        return completableDeferred.await()
    }

    // 플레이어 데스 이벤트같은거는 EntityEvent 임
    private val Event.safePlayer: Player?
        get() {
            if (this is PlayerEvent) return this.player
            if (this is EntityEvent && this.entity is Player) return this.entity as Player
            // 데미지 받은놈은 위에서 처리함
            if (this is EntityDamageByEntityEvent && this.damager is Player) return this.damager as Player
            @Suppress("UNCHECKED_CAST")
            val getter = this::class.memberProperties
                .find { it.name == "player" } as KProperty1<Event, Player>?
            return getter?.get(this)
        }

    suspend fun <T: Event> listener(
        clazz: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): T {
        val listener = object: Listener {  }
        val completableDeferred = CompletableDeferred<T>()
        plugin.server.pluginManager.registerEvent(clazz, listener, priority, { _, event ->
            if (!ignoreCancelled || (event is Cancellable && !event.isCancelled)) {
                @Suppress("UNCHECKED_CAST")
                if (filter(event as T)) {
                    completableDeferred.complete(event)
                }
            }
        }, plugin)
        return completableDeferred.await()
    }

    fun <T : Event> listenEvents(
        clazz: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
    ): Flow<T> {
        val listener = object: Listener {  }
        return flow {
            plugin.server.pluginManager.registerEvent(clazz, listener, priority, { _, event ->
                if (!ignoreCancelled || (event is Cancellable && !event.isCancelled)) {
                    plugin.launch {
                        @Suppress("UNCHECKED_CAST")
                        emit(event as T)
                    }
                }
            }, plugin)
        }
    }
}

suspend inline fun <reified T: Event> Player.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline filter: (T) -> Boolean = { true },
) = ListeningUtil.listener(this, T::class.java, priority, ignoreCancelled, filter)

suspend inline fun <reified T: Event> listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline filter: (T) -> Boolean = { true },
) = ListeningUtil.listener(T::class.java, priority, ignoreCancelled, filter)

inline fun <reified T: Event> events(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
) = ListeningUtil.listenEvents(T::class.java, priority, ignoreCancelled)