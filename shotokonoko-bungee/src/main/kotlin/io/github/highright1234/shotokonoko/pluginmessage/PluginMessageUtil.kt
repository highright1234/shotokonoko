package io.github.highright1234.shotokonoko.pluginmessage

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

fun ProxiedPlayer.send(channel: MessageChannel, block: ByteArrayDataOutput.() -> Unit = {}) {
    val bytes = PluginMessageUtil.bytes {
        block()
    }
    sendData(channel.channel, bytes)
}

fun ByteArrayDataOutput.writeVarInt(value: Int) {
    var value = value
    var part: Int
    while (true) {
        part = value and 0x7F
        value = value ushr 7
        if (value != 0) {
            part = part or 0x80
        }
        writeByte(part)
        if (value == 0) {
            break
        }
    }
}


fun ByteArrayDataInput.readVarInt(maxBytes: Int = 5): Int {
    var out = 0
    var bytes = 0
    var `in`: Byte
    while (true) {
        `in` = readByte()
        out = out or (`in`.toInt() and 0x7F shl bytes++) * 7
        if (bytes > maxBytes) {
            throw RuntimeException("VarInt too big")
        }
        if (`in`.toInt() and 0x80 != 0x80) {
            break
        }
    }
    return out
}

object PluginMessageUtil {

    fun bytes(block: ByteArrayDataOutput.() -> Unit): ByteArray {
        @Suppress("UnstableApiUsage")
        val output = ByteStreams.newDataOutput()
        output.block()
        return output.toByteArray()
    }

    fun listenOnce(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: ProxiedPlayer) -> Unit) {
        lateinit var pluginMessageL: PluginMessageL
        val runnable: ByteArrayDataInput.(player: ProxiedPlayer) -> Unit = {
            block(it)
            plugin.proxy.pluginManager.unregisterListener(pluginMessageL)
        }
        pluginMessageL = PluginMessageL(messageChannel, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listen(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: ProxiedPlayer) -> Unit) {
        messageChannel.registerIncoming(PluginMessageL(messageChannel, block))
    }

    private class PluginMessageL(
        private val messageChannel: MessageChannel,
        private val block: ByteArrayDataInput.(player: ProxiedPlayer) -> Unit
    ): Listener {
        @EventHandler
        fun PluginMessageEvent.on() {
            if (tag != messageChannel.channel || sender !is Server || receiver !is ProxiedPlayer) return
            @Suppress("UnstableApiUsage")
            val input: ByteArrayDataInput = ByteStreams.newDataInput(data)
            input.block(receiver as ProxiedPlayer)
        }
    }
}