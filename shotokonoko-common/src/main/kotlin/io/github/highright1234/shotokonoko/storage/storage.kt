package io.github.highright1234.shotokonoko.storage

import io.github.highright1234.shotokonoko.storage.impl.json.JsonDataStoreProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


fun getDataStore(name: String) = Storage.defaultProvider.getDataStore(name)

suspend fun getDataStoreAsync(name: String) =
    withContext(Dispatchers.IO) { getDataStore(name) }

fun getDataStore(player: UUID) = Storage.defaultProvider.getStore(player.toString())

suspend fun getDataStoreAsync(player: UUID) =
    withContext(Dispatchers.IO) { getDataStore(player) }

fun <T : DataStore> DataStoreProvider<T>.getDataStore(name: String): T
    = getStore(name)

object Storage {

    val dataStoreProviders = Collections.synchronizedList(mutableListOf<DataStoreProvider<*>>(
        JsonDataStoreProvider
    ))
    var defaultProvider = JsonDataStoreProvider

    fun registerProvider(dataStoreProvider: DataStoreProvider<*>) {
        dataStoreProviders += dataStoreProvider
    }

}