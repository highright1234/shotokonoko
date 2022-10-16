package io.github.highright1234.shotokonoko

import io.github.highright1234.shotokonoko.collections.PlayerGCProcessor
import org.bukkit.plugin.java.JavaPlugin

object Shotokonoko {

    private lateinit var _plugin: JavaPlugin
    val plugin: JavaPlugin get() = _plugin

    fun register(plugin: JavaPlugin) {
        _plugin = plugin
        PlayerGCProcessor.register()
    }
}