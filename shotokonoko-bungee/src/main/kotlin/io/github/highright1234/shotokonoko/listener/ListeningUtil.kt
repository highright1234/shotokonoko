package io.github.highright1234.shotokonoko.listener

import com.github.shynixn.mccoroutine.bungeecord.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
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
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

@Suppress("unused")
object ListeningUtil {

    // 바이트코드 여러번 만드는거 방지용 TODO
    private val listeners: ConcurrentHashMap<Class<*>, Class<*>> = ConcurrentHashMap()

    private fun registerListener(listener: Listener) = plugin.proxy.pluginManager.registerListener(plugin, listener)
    private fun unregisterListener(listener : Listener) = plugin.proxy.pluginManager.unregisterListener(listener)


    fun <T: Event> listener(
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
        block: (T) -> Unit,
    ): Listener {


        lateinit var listener: Listener
        listener = object: Any() {
            fun on(event: T) {
                if (event is Cancellable && event.isCancelled && ignoreCancelled) return
                if (!filter(event)) return
                block(event)
                unregisterListener(listener)
            }
        }.let { newListener(clazz, priority, it) }

        return listener
    }

    fun <T : Event> listener(
        player: ProxiedPlayer,
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
        block: (Result<T>) -> Unit,
    ): Listener {

        lateinit var listener: Listener
        lateinit var quitListener: Listener

        listener = object: Any() {
            fun on(event: T) {
                if (event is Cancellable && event.isCancelled && ignoreCancelled) return
                if (filter(event) && event.safePlayer == player) {
                    block(Result.success(event))
                    unregisterListener(listener)
                    unregisterListener(quitListener)
                }
            }
        }.let { newListener(clazz, priority, it) }

        quitListener = PlayerQuitL(player, listener, block)
            .also { registerListener(it) }

        return listener
    }

    suspend fun <T : Event> listener(
        player: ProxiedPlayer,
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): Result<T> {
        return suspendCancellableCoroutine { continuation ->
            val listener = listener(player, clazz, priority, ignoreCancelled, filter) {
                continuation.resume(it)
            }
            continuation.invokeOnCancellation {
                unregisterListener(listener)
            }
        }
    }

    // 플레이어 데스 이벤트같은거는 EntityEvent 임
    private val Event.safePlayer: ProxiedPlayer?
        get() {

            val getter = this::class.java.methods
                .find { it.name == "getPlayer" }
            val field = this::class.java.declaredFields
                .find { it.name == "player" }

            return (getter?.invoke(this) ?: field?.apply { isAccessible = true }?.get(this)) as? ProxiedPlayer
        }

    suspend fun <T: Event> listener(
        clazz: Class<T>,
        priority: Byte = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        filter: (T) -> Boolean = { true },
    ): T {
        return suspendCancellableCoroutine { continuation ->
            val listener = listener(clazz, priority, ignoreCancelled, filter) {
                continuation.resume(it)
            }
            continuation.invokeOnCancellation {
                unregisterListener(listener)
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
        return ByteBuddy() // TODO 캐쉬 기능
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


    internal class PlayerQuitL<T>(
        private val player: ProxiedPlayer,
        private val listener: Listener,
        private val block: (Result<T>) -> Unit
    ): Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun on(event: PlayerDisconnectEvent) {
            if (player != event.player) return
            block(Result.failure(PlayerQuitException()))
            plugin.proxy.pluginManager.unregisterListener(listener)
            plugin.proxy.pluginManager.unregisterListener(this)
        }
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