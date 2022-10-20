package io.github.highright1234.shotokonoko

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.PluginClassLoader

object Shotokonoko {

    private val _plugin: JavaPlugin by lazy {
        val loaderField = PluginClassLoader::class.java
            .getDeclaredField("libraryLoader")
            .apply { isAccessible = true }
        Bukkit.getPluginManager().plugins
            .first { loaderField[it.javaClass.classLoader] == Shotokonoko::class.java.classLoader } as JavaPlugin
    }
    val plugin: JavaPlugin get() = _plugin
}