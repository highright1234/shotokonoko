package io.github.highright1234.shotokonoko

import com.github.shynixn.mccoroutine.bungeecord.bungeeCordDispatcher
import com.github.shynixn.mccoroutine.bungeecord.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Suppress("Unused")
object PlatformManagerImpl {
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return Shotokonoko.plugin.launch { block() }
    }
    val pluginLoader: Any get() = Shotokonoko.plugin.proxy.pluginManager
    val asyncDispatcher: CoroutineContext get() = Shotokonoko.plugin.bungeeCordDispatcher
    val plugin: Any get() = Shotokonoko.plugin
}