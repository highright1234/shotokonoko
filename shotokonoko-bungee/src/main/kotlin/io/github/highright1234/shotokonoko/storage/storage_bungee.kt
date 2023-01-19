package io.github.highright1234.shotokonoko.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.md_5.bungee.api.connection.ProxiedPlayer

fun getDataStore(player: ProxiedPlayer) = getDataStore(player.uniqueId)

suspend fun getDataStoreAsync(player: ProxiedPlayer) =
    withContext(Dispatchers.IO) { getDataStore(player) }