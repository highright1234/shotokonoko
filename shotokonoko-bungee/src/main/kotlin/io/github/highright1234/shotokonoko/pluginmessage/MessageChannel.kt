package io.github.highright1234.shotokonoko.pluginmessage

import io.github.highright1234.shotokonoko.Shotokonoko.plugin

data class MessageChannel(val channel: String) {
    fun init() {
        plugin.proxy.registerChannel(channel)
    }
}