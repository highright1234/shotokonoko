package io.github.highright1234.shotokonoko.collections

import org.bukkit.entity.Player
import java.util.*

inline fun <T> newPlayerHashMapOf(vararg pairs: Pair<Player, T>): MutableMap<Player, T> {
    return Collections.synchronizedMap(hashMapOf(*pairs))
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun <T> playerHashMapOf(vararg pairs: Pair<Player, T>): MutableMap<Player, T> {
    return Collections.synchronizedMap(hashMapOf(*pairs))
        .also { PlayerGCProcessor.addTarget(it) }
}