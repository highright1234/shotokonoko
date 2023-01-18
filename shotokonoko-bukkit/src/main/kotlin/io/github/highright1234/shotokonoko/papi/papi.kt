package io.github.highright1234.shotokonoko.papi

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.papi.node.PapiNode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.OfflinePlayer

fun papi(block: PapiNode.() -> Unit) {
    if (existPlaceholderAPI) {
        PapiNode.block()
    }
}

private val existPlaceholderAPI get() = plugin.server.pluginManager.getPlugin("PlaceholderAPI") != null

fun ppapi(input: String, player: OfflinePlayer? = null): String {
    // 클래스로더 나쁜놈
    return plugin.javaClass.classLoader
        .loadClass("me.clip.placeholderapi.PlaceholderAPI")
        .getMethod("setPlaceholders", OfflinePlayer::class.java, String::class.java)
        .invoke(null, player, input) as String
}

fun ppapi(input: Component, player: OfflinePlayer? = null): Component {
    val string: String = GsonComponentSerializer.gson().serialize(input)
    return GsonComponentSerializer.gson().deserialize(ppapi(string, player))
}