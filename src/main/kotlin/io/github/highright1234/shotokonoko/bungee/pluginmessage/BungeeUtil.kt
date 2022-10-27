package io.github.highright1234.shotokonoko.bungee.pluginmessage

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.bungee.BungeePlayer
import io.github.highright1234.shotokonoko.bungee.BungeeServer
import io.github.highright1234.shotokonoko.bungee.MessageChannel
import io.github.highright1234.shotokonoko.bungee.pluginmessage.PluginMessageUtil.bytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import java.net.InetAddress
import java.util.*


@Suppress("DeferredIsResult")
object BungeeUtil {

    private val callables = mutableMapOf<String, ByteArrayDataInput.() -> Unit>()

    private fun <T> bungee(subchannel: String = "", block: (subchannel: String) -> T): T {
        requiresBungee()
        initBungeeSettings()
        return block(subchannel)
    }

    private fun <T> response(
        identifier: String,
        block: ByteArrayDataInput.() -> T
    ): Deferred<T> {
        val completableDeferred = CompletableDeferred<T>()
        callables[identifier] = {
            block().let(completableDeferred::complete)
        }
        return completableDeferred
    }


    private fun <T> response(
        subchannel: String,
        identifier: String,
        block: ByteArrayDataInput.() -> T
    ): Deferred<T> = response("$subchannel-$identifier", block)

    private fun requiresBungee() {
        require(isBungee) { "Server should be running with bungee" }
    }

    private fun initBungeeSettings() {
        MessageChannel("BungeeCord").registerOutgoing()
        registerResponseListener()
    }

    private val randomPlayer get() = plugin.server.onlinePlayers.random()

    private fun send(
        subchannel: String,
        block: ByteArrayDataOutput.() -> Unit = {}
    ) = randomPlayer.send(subchannel, block)

    private fun Player.send(
        subchannel: String,
        block: ByteArrayDataOutput.() -> Unit = {}
    ) = bungee(subchannel) {
        val bytes = bytes {
            writeUTF(subchannel)
            block()
        }
        sendPluginMessage(plugin, "BungeeCord", bytes)
    }

    fun connect(player: Player, server: BungeeServer) = player.send(Subchannels.CONNECT) {
        writeUTF(server.name)
    }

    fun connect(player: BungeePlayer, server: BungeeServer) = send(Subchannels.CONNECT_OTHER) {
        writeUTF(player.name)
        writeUTF(server.name)
    }

    fun ip(player: Player): Deferred<InetAddress> = bungee(Subchannels.IP) {
        player.send(it)
        response(it, player.name) {
            readUTF().let(InetAddress::getByName)
        }
    }

    fun ipOther(player: BungeePlayer): Deferred<InetAddress> = bungee(Subchannels.IP_OTHER) {
        send(it) {
            writeUTF(player.name)
        }
        response(it, player.name) {
            readUTF().let(InetAddress::getByName)
        }
    }

    fun playerCount(server: BungeeServer): Deferred<Int> = bungee(Subchannels.PLAYER_COUNT) {
        send(it) {
            writeUTF(server.name)
        }
        response(it, server.name) {
            readInt()
        }
    }

    fun playerList(server: BungeeServer): Deferred<List<BungeePlayer>> = bungee(Subchannels.PLAYER_LIST) {
        send(it) {
            writeUTF(server.name)
        }
        response(it, server.name) {
            readUTF()
                .split(", ")
                .map(::BungeePlayer)
        }
    }

    fun getServers(): Deferred<List<BungeeServer>> = bungee(Subchannels.GET_SERVERS) {
        send(it)
        response(it) {
            readUTF()
                .split(", ")
                .map(::BungeeServer)
        }
    }

    fun message(player: BungeePlayer, message: String) = send(Subchannels.MESSAGE) {
        writeUTF(player.name)
        writeUTF(message)
    }

    fun message(player: BungeePlayer, message: Component) = send(Subchannels.MESSAGE_RAW) {
        writeUTF(player.name)
        message.let { GsonComponentSerializer.gson().serialize(message) }.let(::writeUTF)
    }

    fun getServer(): Deferred<BungeeServer> = bungee(Subchannels.GET_SERVER) {
        send(it)
        response(it) {
            readUTF().let(::BungeeServer)
        }
    }

    fun foward(server: BungeeServer, channel: MessageChannel, bytes: ByteArray) = send(Subchannels.FORWARD) {
        writeUTF(server.name)
        writeUTF(channel.channel)
        writeShort(bytes.size)
        write(bytes)
    }

    fun forwardToPlayer(player: BungeePlayer, channel: MessageChannel, bytes: ByteArray) = send(Subchannels.FORWARD_TO_PLAYER) {
        writeUTF(player.name)
        writeUTF(channel.channel)
        writeShort(bytes.size)
        write(bytes)
    }

    fun uuid(player: Player): Deferred<UUID> = bungee(Subchannels.UUID) {
        player.send(it)
        response(it, player.name) {
            readUTF().let(UUID::fromString)
        }
    }

    fun uuidOther(player: BungeePlayer): Deferred<UUID> = bungee(Subchannels.UUID_OTHER) {
        send(it) {
            writeUTF(player.name)
        }
        response(it, player.name) {
            readUTF().let(UUID::fromString)
        }
    }

    fun serverIp(server: BungeeServer): Deferred<InetAddress> = bungee(Subchannels.SERVER_IP) {
        send(it) {
            writeUTF(server.name)
        }
        response(it, server.name) {
            readUTF().let(InetAddress::getByName)
        }
    }

    fun kickPlayer(player: BungeePlayer) = send(Subchannels.KICK_PLAYER) { writeUTF(player.name) }

    val isBungee: Boolean get() {
        val server = plugin.server
        val isBungee = server.spigot().spigotConfig.getBoolean("settings.bungeecord")
        val isVelocity: Boolean = if (server.minecraftVersion.split(".")[1].toInt() <= 18) {
            server.spigot().paperConfig.getBoolean("settings.velocity-support.enabled")
        } else {
            server.spigot().paperConfig.getBoolean("proxies.velocity.enabled")
        }
        require((!isBungee && !isVelocity) || isBungee != isVelocity) {
            "Only one of the bungeecord settings and the velocity setting must be on."
        }
        return isBungee || isVelocity
    }

    private fun registerResponseListener() = PluginMessageUtil.listen(MessageChannel("BungeeCord")) {
        val identifierGroup = Subchannels.run {
            listOf(
                IP_OTHER,
                PLAYER_COUNT,
                PLAYER_LIST,
                FORWARD,
                FORWARD_TO_PLAYER,
                UUID_OTHER,
                SERVER_IP,
            )
        }
        val subchannel: String = readUTF()
        var identifier: String? = null
        if (subchannel in identifierGroup) {
            identifier = readUTF()
        }
        val key = identifier?.let { "$subchannel-$identifier" } ?: subchannel
        callables.remove(key)?.invoke(this)
    }

    private object Subchannels {
        const val CONNECT = "Connect"
        const val CONNECT_OTHER = "ConnectOther"
        const val IP = "IP"
        const val IP_OTHER = "IPOther" //
        const val PLAYER_COUNT = "PlayerCount" //
        const val PLAYER_LIST = "PlayerList" //
        const val GET_SERVERS = "GetServers"
        const val MESSAGE = "Message"
        const val MESSAGE_RAW = "MessageRaw"
        const val GET_SERVER = "GetServer"
        const val FORWARD = "Forward" //
        const val FORWARD_TO_PLAYER = "ForwardToPlayer" //
        const val UUID = "UUID"
        const val UUID_OTHER = "UUIDOther" //
        const val SERVER_IP = "ServerIP" //
        const val KICK_PLAYER = "KickPlayer"
    }
}