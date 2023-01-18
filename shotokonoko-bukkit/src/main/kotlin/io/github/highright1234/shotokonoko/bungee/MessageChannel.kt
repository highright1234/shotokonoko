package io.github.highright1234.shotokonoko.bungee

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import org.bukkit.plugin.messaging.PluginMessageListener

data class MessageChannel(val channel: String) {
    fun registerIncoming(pluginMessageListener: PluginMessageListener) {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, channel, pluginMessageListener)
    }

    fun registerOutgoing() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel)
    }
}