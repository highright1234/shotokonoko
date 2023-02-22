package io.github.highright1234.shotokonokodebug

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.mongodb.MongoCredential
import com.outstandingboy.donationalert.entity.Donation
import io.github.highright1234.shotokonoko.bungee.MessageChannel
import io.github.highright1234.shotokonoko.bungee.pluginmessage.send
import io.github.highright1234.shotokonoko.collections.newPlayerArrayList
import io.github.highright1234.shotokonoko.listener.ChatScanner
import io.github.highright1234.shotokonoko.listener.events
import io.github.highright1234.shotokonoko.listener.exception.PlayerQuitException
import io.github.highright1234.shotokonoko.listener.listen
import io.github.highright1234.shotokonoko.loader.DynamicLoader
import io.github.highright1234.shotokonoko.plus
import io.github.highright1234.shotokonoko.storage.Storage
import io.github.highright1234.shotokonoko.storage.impl.mongodb.MongoDataStoreProvider
import io.github.highright1234.shotokonokodebug.config.MongoConfig
import io.github.monun.kommand.kommand
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import org.bukkit.event.player.PlayerJoinEvent
import java.net.InetSocketAddress

@Suppress("Unused")
class ShotokonokoDebug: SuspendingJavaPlugin() {
    private val pluginMessageChannel = MessageChannel("shotokonoko-debug:test")
    private var donation: Donation? = null
    override suspend fun onEnableAsync() {
        DynamicLoader.load(
            listOf("jitpack" to "https://jitpack.io"),
            listOf("com.github.outstanding1301:donation-alert-api:1.0.0")
        )

        donation = Donation().apply {
            nickName = "하라"
            comment = "바부"
            amount = 1000L
            id = "HighRight"
        }
        logger.info("$donation")
        logger.info("${server.pluginManager.getPlugin("PlaceholderAPI") != null}")
        MongoConfig.apply {
            load("mongo.yml")
            if (enable) {
                val credential = MongoConfig.User.run {
                    MongoCredential.createCredential(username, database, password.toCharArray())
                }
                val storage = MongoDataStoreProvider().apply {
                    start(
                        InetSocketAddress(address, port),
                        credential,
                        MongoConfig.database,
                        MongoConfig.collection
                    )
                }
                Storage.dataStoreProviders += storage
                Storage.defaultProvider = storage
            }
        }
        TestPAPI.register()
        kommand {
            TestKommand.register(this)
            StorageTestKommand.register(this)
        }
        pluginMessageChannel.registerOutgoing()
        launchPlayerGCChecker()
        launchPsycho()
        launchPluginMessageTester()
    }

    private fun launchPluginMessageTester() = launch {
        events<PlayerJoinEvent>().collect { event ->
            println("sdfkjbsdfbjsdfjblsdf")
            println(server.messenger.outgoingChannels)
            println(server.messenger)
            delay(1) // 몰랐는데 바로 못보냄
            event.player.send(pluginMessageChannel) {
                writeUTF("tasdfsdfljksdfbnsfd")
                println("ㅇㅋㅋㅅㅋㅅ")
            }
            println("sended")
        }
    }

    private fun launchPlayerGCChecker() = launch {
        val arrayList = newPlayerArrayList()
        launch {
            while (true) {
                server.broadcast(Component.text(arrayList.joinToString()))
                delay(1000)
            }
        }
        events<PlayerJoinEvent>().collect {
            arrayList += it.player
        }
    }

    private fun launchPsycho() = launch {

        val event = withTimeoutOrNull<PlayerJoinEvent>(60000L) {
            listen()
        } ?: return@launch
        val player = event.player
        player.sendMessage("채팅 아무거나 써보셈")

        ChatScanner(player).await()
            .onSuccess {
                player.sendMessage(it + "라고 보냈구만")
            }.onFailure {
                val message = when (it) {
                    is PlayerQuitException -> "플레이어 쉐키가 나감"
                    is TimeoutCancellationException -> "1분 지났다"
                    else -> "버근가"
                }
                player.sendMessage(message)
            }
    }
}