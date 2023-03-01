package io.github.highright1234.shotokonoko.bungee.pluginmessage

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.bungee.MessageChannel
import io.github.highright1234.shotokonoko.listener.ListeningUtil
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.messaging.PluginMessageListener

fun Player.send(channel: MessageChannel, block: ByteArrayDataOutput.() -> Unit = {}) {
    channel.registerOutgoing()
    val bytes = PluginMessageUtil.bytes(block)
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

    fun listenOnce( // 플레이어 나가는건 알아서 확인해야함
        player: Player,
        messageChannel: MessageChannel,
        subChannel: String?,
        filter: ByteArrayDataInput.(player: Player) -> Boolean,
        block: ByteArrayDataInput.(player: Player) -> Unit
    ) {
        lateinit var pluginMessageL: PluginMessageL
        lateinit var listener: Listener
        val runnable: ByteArrayDataInput.(player: Player) -> Unit = {
            block(it)
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
            HandlerList.unregisterAll(listener)
        }
        listener = ListeningUtil.listener(PlayerQuitEvent::class.java, filter = { it.player == player }) {
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
            HandlerList.unregisterAll(listener)
        }
        val checker: ByteArrayDataInput.(player: Player) -> Boolean = { sender ->
            player == sender && filter(player)
        }
        pluginMessageL = PluginMessageL(messageChannel, subChannel, checker, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listenOnce(
        messageChannel: MessageChannel,
        subChannel: String?,
        filter: ByteArrayDataInput.(player: Player) -> Boolean,
        block: ByteArrayDataInput.(player: Player) -> Unit
    ) {
        lateinit var pluginMessageL: PluginMessageL
        val runnable: ByteArrayDataInput.(player: Player) -> Unit = {
            block(it)
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
        }

        pluginMessageL = PluginMessageL(messageChannel, subChannel, filter, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listenOnce(
        messageChannel: MessageChannel,
        subChannel: String?,
        block: ByteArrayDataInput.(player: Player) -> Unit
    ) {
        lateinit var pluginMessageL: PluginMessageL
        val runnable: ByteArrayDataInput.(player: Player) -> Unit = {
            block(it)
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
        }
        pluginMessageL = PluginMessageL(messageChannel, subChannel, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listenOnce(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: Player) -> Unit) {
        lateinit var pluginMessageL: PluginMessageL
        val runnable: ByteArrayDataInput.(player: Player) -> Unit = {
            block(it)
            plugin.server.messenger.unregisterIncomingPluginChannel(plugin, messageChannel.channel, pluginMessageL)
        }
        pluginMessageL = PluginMessageL(messageChannel, null, runnable)
        messageChannel.registerIncoming(pluginMessageL)
    }

    fun listen(
        messageChannel: MessageChannel,
        subChannel: String?,
        block: ByteArrayDataInput.(player: Player) -> Unit
    ) {
        messageChannel.registerIncoming(PluginMessageL(messageChannel, subChannel, block))
    }

    @Deprecated(message = "deprecated", replaceWith = ReplaceWith("listen(messageChannel, null, block)"))
    fun listen(messageChannel: MessageChannel, block: ByteArrayDataInput.(player: Player) -> Unit) {
        listen(messageChannel, null, block)
    }

    @Suppress("UnstableApiUsage")
    private class PluginMessageL(
        private val messageChannel: MessageChannel,
        private val subChannel: String?,
        private val filter: ByteArrayDataInput.(player: Player) -> Boolean = { true },
        private val block: ByteArrayDataInput.(player: Player) -> Unit
    ): PluginMessageListener {
        constructor(
            messageChannel: MessageChannel,
            subChannel: String?,
            block: ByteArrayDataInput.(player: Player) -> Unit
        ): this(messageChannel, subChannel, { true }, block)
        override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
            if (channel != messageChannel.channel) {
                return
            }
            val forFiler = ByteStreams.newDataInput(message).apply { if (subChannel != null) readUTF() }
            if (!filter(forFiler, player)) return
            val input: ByteArrayDataInput = ByteStreams.newDataInput(message)
            subChannel?.let { subchan ->
                if (input.readUTF() != subchan) return
            }
            input.block(player)
        }
    }
}