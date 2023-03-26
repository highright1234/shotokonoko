package io.github.highright1234.shotokonoko.collections

import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

inline fun <T> newPlayerHashMapOf(vararg pairs: Pair<ProxiedPlayer, T>): MutableMap<ProxiedPlayer, T> {
    return Collections.synchronizedMap(hashMapOf(*pairs))
        .also { PlayerGCProcessor.addTarget(it) }
}

inline fun <T> playerHashMapOf(vararg pairs: Pair<ProxiedPlayer, T>): MutableMap<ProxiedPlayer, T> {
    return Collections.synchronizedMap(hashMapOf(*pairs))
        .also { PlayerGCProcessor.addTarget(it) }
}