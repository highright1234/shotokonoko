package io.github.highright1234.shotokonoko.collections

import io.github.highright1234.shotokonoko.Shotokonoko
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.lang.ref.WeakReference

object PlayerGCProcessor {

    private var isRegistered = false
    private fun register() {
        if (!isRegistered) {
            val plugin = Shotokonoko.plugin
            plugin.server.pluginManager.registerEvents(QuitL, plugin)
            isRegistered = true
        }
    }

    private val targetMaps = mutableListOf<WeakReference<MutableMap<Player, *>>>()
    private val targetCollections = mutableListOf<WeakReference<MutableCollection<Player>>>()

    fun addTarget(collection: MutableCollection<Player>) {
        register()
        targetCollections += WeakReference(collection)
    }

    fun addTarget(map: MutableMap<Player, *>) {
        register()
        targetMaps += WeakReference(map)
    }

    private object QuitL : Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        fun PlayerQuitEvent.on() {
            targetCollections.toList().forEach {
                it.get()?.remove(player) ?: targetCollections.remove(it)
            }
            targetMaps.toList().forEach {
                it.get()?.remove(player) ?: targetMaps.remove(it)
            }
        }
    }
}