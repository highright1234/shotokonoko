package io.github.highright1234.shotokonoko

import io.github.highright1234.shotokonoko.storage.AutoSaver
import io.github.highright1234.shotokonoko.storage.Storage
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

object Shotokonoko {

    private val _plugin: Plugin by lazy {
        val loaderField = Class.forName("net.md_5.bungee.api.plugin.PluginClassloader")
            .getDeclaredField("libraryLoader")
            .apply { isAccessible = true }
        val out = ProxyServer.getInstance().pluginManager.plugins
            .first { loaderField[it.javaClass.classLoader] == Shotokonoko::class.java.classLoader } as Plugin
        out
    }
    val plugin: Plugin get() = _plugin

    fun disable() {
        AutoSaver.isEnabled = false

        Storage.dataStoreProviders.forEach {
            it.removeAllStoreCaches()
        }
    }



//    private object DataCleaner: Listener {
//        @EventHandler(priority = EventPriority.HIGHEST)
//        fun PluginDisableEvent.on() {
//            if (plugin != Shotokonoko.plugin) return
//            AutoSaver.isEnabled = false
//            Storage.dataStoreProviders.forEach {
//                it.removeAllStoreCaches()
//            }
//        }
//    }
}