package io.github.highright1234.shotokonoko.monun

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.listener.events
import io.github.highright1234.shotokonoko.listener.listen
import io.github.monun.tap.fake.FakeEntityServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.PluginDisableEvent

fun FakeEntityServer.init(player: Player) {
    addPlayer(player)
    plugin.launch {
        while (player.isOnline) {
            update()
            delay(1)
        }
    }
}

fun FakeEntityServer.init(predicate: ((player: Player) -> Boolean) = { true }) {
    plugin.run {
        server.onlinePlayers.forEach(::addPlayer)
        launch {
            events<PlayerJoinEvent>()
                .map { it.player }
                .filter(predicate)
                .collect { addPlayer(it) }
        }
        launch {
            while (true) {
                update()
                delay(1)
            }
        }
        launch {
            listen<PluginDisableEvent> { it.plugin == plugin } // blocking
            server.onlinePlayers.forEach(::removePlayer)
        }
    }
}