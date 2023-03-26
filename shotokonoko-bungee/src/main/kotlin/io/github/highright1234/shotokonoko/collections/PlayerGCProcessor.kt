package io.github.highright1234.shotokonoko.collections

import io.github.highright1234.shotokonoko.Shotokonoko
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.lang.ref.WeakReference

object PlayerGCProcessor {

    private var isRegistered = false
    private fun register() {
        if (!isRegistered) {
            val plugin = Shotokonoko.plugin
            plugin.proxy.pluginManager.registerListener(plugin, QuitL)
            isRegistered = true
        }
    }

    private val targetMaps = mutableListOf<WeakReference<MutableMap<ProxiedPlayer, *>>>()
    private val targetCollections = mutableListOf<WeakReference<MutableCollection<ProxiedPlayer>>>()

    fun addTarget(collection: MutableCollection<ProxiedPlayer>) {
        register()
        targetCollections += WeakReference(collection)
    }

    fun addTarget(map: MutableMap<ProxiedPlayer, *>) {
        register()
        targetMaps += WeakReference(map)
    }

    internal object QuitL : Listener { // 자바상에서 public 이여야 함
        @EventHandler(priority = EventPriority.HIGHEST)
        fun PlayerDisconnectEvent.on() {
            targetCollections.toList().forEach {
                it.get()?.remove(player) ?: targetCollections.remove(it)
            }
            targetMaps.toList().forEach {
                it.get()?.remove(player) ?: targetMaps.remove(it)
            }
        }
    }
}