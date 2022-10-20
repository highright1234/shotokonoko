package io.github.highright1234.shotokonoko.papi

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ExpansionRequestContext(val offlinePlayer: OfflinePlayer?) {
    val player: Player? get() = if (offlinePlayer is Player) offlinePlayer else null
    var arguments = mapOf<String, String>()
    internal set
}