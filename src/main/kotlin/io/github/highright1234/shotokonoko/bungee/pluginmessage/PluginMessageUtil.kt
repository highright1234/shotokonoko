package io.github.highright1234.shotokonoko.bungee.pluginmessage

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.bungee.MessageChannel
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

fun Player.send(channel: MessageChannel, block: ByteArrayDataOutput.() -> Unit = {}) {
    channel.registerOutgoing()
    val bytes = PluginMessageUtil.bytes {
        block()
    }
    sendPluginMessage(plugin, channel.channel, bytes)
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

    fun listenOnce(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: Player) -> Unit) {
        lateinit var pluginMessageL: PluginMessageL
        val runnable: ByteArrayDataInput.(player: Player) -> Unit = {
            block(it)
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
        }
        pluginMessageL = PluginMessageL(messageChannel, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listen(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: Player) -> Unit) {
        messageChannel.registerIncoming(PluginMessageL(messageChannel, block))
    }

    private class PluginMessageL(
        private val messageChannel: MessageChannel,
        private val block: ByteArrayDataInput.(player: Player) -> Unit
    ): PluginMessageListener {
        override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
            if (channel != messageChannel.channel) {
                return
            }
            @Suppress("UnstableApiUsage")
            val input: ByteArrayDataInput = ByteStreams.newDataInput(message)
            input.block(player)
        }
    }
}