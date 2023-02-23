package io.github.highright1234.shotokonoko.listener

import com.github.shynixn.mccoroutine.bungeecord.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Cancellable
import net.md_5.bungee.api.plugin.Event
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.lang.reflect.Modifier
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Suppress("unused")
object ListeningUtil {

    fun <T: Event> listener(
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
        block: (T) -> Unit,
    ) {

        lateinit var listener: Listener
        listener = object: Any() {
            fun on(event: T) {
                if (event is Cancellable && event.isCancelled && ignoreCancelled) return
                if (!filter(event)) return
                block(event)
                plugin.proxy.pluginManager.unregisterListener(listener)
            }
        }.let { newListener(clazz, priority, it) }

    }

    fun <T : Event> listener(
        player: ProxiedPlayer,
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
        block: (Result<T>) -> Unit,
    ) {
        lateinit var listener: Listener
        lateinit var exitListener: Listener

        listener = object: Any() {
            fun on(event: T) {
                if (event is Cancellable && event.isCancelled && ignoreCancelled) return
                if (filter(event) && event.safePlayer == player) {
                    block(Result.success(event))
                    plugin.proxy.pluginManager.unregisterListener(listener)
                    plugin.proxy.pluginManager.unregisterListener(exitListener)
                }
            }
        }.let { newListener(clazz, priority, it) }


        exitListener = object: Listener {
            fun on(event: PlayerDisconnectEvent) {
                if (player != event.player) return
                block(Result.failure(PlayerQuitException()))
                plugin.proxy.pluginManager.unregisterListener(listener)
                plugin.proxy.pluginManager.unregisterListener(exitListener)
            }
        }.let { newListener(PlayerDisconnectEvent::class.java, EventPriority.HIGHEST, it) }

    }

    suspend fun <T : Event> listener(
        player: ProxiedPlayer,
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): Result<T> {
        return suspendCoroutine { continuation ->
            listener(player, clazz, priority, ignoreCancelled, filter) {
                continuation.resume(it)
            }
        }
    }

    // 플레이어 데스 이벤트같은거는 EntityEvent 임
    private val Event.safePlayer: ProxiedPlayer?
        get() {
            @Suppress("UNCHECKED_CAST")
            val getter = this::class.memberProperties
                .find { it.name == "player" } as KProperty1<Event, ProxiedPlayer>?
            return getter?.get(this)
        }

    suspend fun <T: Event> listener(
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): T {
        return suspendCoroutine { continuation ->
            listener(clazz, priority, ignoreCancelled, filter) {
                continuation.resume(it)
            }
        }
    }

    fun <T : Event> listenEvents(
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
    ): SharedFlow<T> {
        val flow = MutableSharedFlow<T>()
        object: Any() {
            fun on(event: T) {
                if (event is Cancellable && event.isCancelled && ignoreCancelled) return
                plugin.launch {
                    flow.emit(event)
                }
            }
        }.let { newListener(clazz, priority, it) }

        return flow.asSharedFlow()
    }

    private fun newListener(clazz: Class<out Event>, priority: Byte, interceptor: Any): Listener {
        return ByteBuddy()
            .subclass(Listener::class.java)
            .modifiers(Modifier.PUBLIC)
            .defineMethod("on", Void.TYPE, Modifier.PUBLIC)
            .withParameters(clazz)
            .intercept(MethodDelegation.to(interceptor))
            .annotateMethod(EventHandler(priority = priority))
            .make()
            .load(javaClass.classLoader)
            .loaded.getConstructor().newInstance()
            .also { plugin.proxy.pluginManager.registerListener(plugin, it) }
    }
}

suspend inline fun <reified T: Event> ProxiedPlayer.listen(
    priority: Byte = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline filter: (T) -> Boolean = { true },
) = ListeningUtil.listener(this, T::class.java, priority, ignoreCancelled, filter)

suspend inline fun <reified T: Event> listen(
    priority: Byte = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline filter: (T) -> Boolean = { true },
) = ListeningUtil.listener(T::class.java, priority, ignoreCancelled, filter)

inline fun <reified T: Event> events(
    priority: Byte = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
) = ListeningUtil.listenEvents(T::class.java, priority, ignoreCancelled)