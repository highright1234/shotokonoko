package io.github.highright1234.shotokonoko.collections

import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

inline fun newPlayerArrayList(vararg players: ProxiedPlayer): MutableList<ProxiedPlayer> {
    return Collections.synchronizedList(players.toMutableList()) // ArrayList
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun newPlayerHashSet(vararg players: ProxiedPlayer): MutableSet<ProxiedPlayer> {
    return players.associateWith { true } // LinkedHashSet
        .let(Collections::newSetFromMap)
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun newPlayerLinkedDeque(vararg players: ProxiedPlayer): Deque<ProxiedPlayer> {
    return ConcurrentLinkedDeque(players.toMutableList())
        .also { PlayerGCProcessor.addTarget(it) }
}
