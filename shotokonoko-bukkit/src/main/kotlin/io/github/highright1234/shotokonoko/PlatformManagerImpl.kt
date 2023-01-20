package io.github.highright1234.shotokonoko

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Suppress("Unused")
object PlatformManagerImpl {
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return Shotokonoko.plugin.launch(asyncDispatcher) { block() }
    }

    val pluginLoader: Any get() = Shotokonoko.plugin.pluginLoader
    val asyncDispatcher: CoroutineContext get() = Shotokonoko.plugin.asyncDispatcher
    val plugin: Any get() = Shotokonoko.plugin
}