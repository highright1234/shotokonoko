package io.github.highright1234.shotokonoko

import io.github.highright1234.shotokonoko.storage.AutoSaver
import io.github.highright1234.shotokonoko.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.PluginClassLoader

object Shotokonoko {

    private val _plugin: JavaPlugin by lazy {
        val loaderField = PluginClassLoader::class.java
            .getDeclaredField("libraryLoader")
            .apply { isAccessible = true }
        val out = Bukkit.getPluginManager().plugins
            .first { loaderField[it.javaClass.classLoader] == Shotokonoko::class.java.classLoader } as JavaPlugin
        init(out)
        out
    }
    val plugin: JavaPlugin get() = _plugin

    private fun init(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(DataCleaner, plugin)
    }

    private object DataCleaner: Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PluginDisableEvent.on() {
            if (plugin != Shotokonoko.plugin) return
            AutoSaver.isEnabled = false
            Storage.dataStoreProviders.forEach {
                it.removeAllStoreCaches()
            }
        }
    }
}