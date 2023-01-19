package io.github.highright1234.shotokonoko.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player

fun getDataStore(player: Player) = getDataStore(player.uniqueId)

suspend fun getDataStoreAsync(player: Player) =
    withContext(Dispatchers.IO) { getDataStore(player) }