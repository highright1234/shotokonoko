package io.github.highright1234.shotokonoko.collections

import org.bukkit.entity.Player
import java.util.*

fun <T> newPlayerHashMapOf(vararg pairs: Pair<Player, T>): MutableMap<Player, T> {
    return Collections.synchronizedMap(pairs.toMap()) // LinkedHashMap
        .also { PlayerGCProcessor.addTarget(it) }
}

fun <T> playerHashMapOf(vararg pairs: Pair<Player, T>): MutableMap<Player, T> {
    return Collections.synchronizedMap(pairs.toMap()) // LinkedHashMap
        .also { PlayerGCProcessor.addTarget(it) }
}