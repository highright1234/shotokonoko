package io.github.highright1234.shotokonoko.collections

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

inline fun newPlayerArrayList(vararg players: Player): MutableList<Player> {
    return Collections.synchronizedList(players.toMutableList()) // ArrayList
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun newPlayerHashSet(vararg players: Player): MutableSet<Player> {
    return players.associateWith { true } // LinkedHashSet
        .let(Collections::newSetFromMap)
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun newPlayerLinkedDeque(vararg players: Player): Deque<Player> {
    return ConcurrentLinkedDeque(players.toMutableList())
        .also { PlayerGCProcessor.addTarget(it) }
}
