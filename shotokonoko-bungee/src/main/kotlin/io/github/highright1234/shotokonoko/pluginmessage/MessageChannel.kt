package io.github.highright1234.shotokonoko.pluginmessage

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import net.md_5.bungee.api.plugin.Listener

data class MessageChannel(val channel: String) {
    fun registerIncoming(listener: Listener) {
        plugin.proxy.registerChannel(channel)
        plugin.proxy.pluginManager.registerListener(plugin, listener)
    }
}