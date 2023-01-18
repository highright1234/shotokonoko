package io.github.highright1234.shotokonoko.collections

import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

fun <T> newPlayerHashMapOf(vararg pairs: Pair<ProxiedPlayer, T>): MutableMap<ProxiedPlayer, T> {
    return Collections.synchronizedMap(pairs.toMap()) // LinkedHashMap
        .also { PlayerGCProcessor.addTarget(it) }
}

fun <T> playerHashMapOf(vararg pairs: Pair<ProxiedPlayer, T>): MutableMap<ProxiedPlayer, T> {
    return Collections.synchronizedMap(pairs.toMap()) // LinkedHashMap
        .also { PlayerGCProcessor.addTarget(it) }
}