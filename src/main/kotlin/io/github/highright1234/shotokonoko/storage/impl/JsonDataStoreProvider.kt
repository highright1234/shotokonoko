package io.github.highright1234.shotokonoko.storage.impl

import io.github.highright1234.shotokonoko.storage.DataStore
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import java.io.File

object JsonDataStoreProvider : DataStoreProvider<JsonDataStore> {

    private val stores = hashMapOf<String, JsonDataStore>()

    init {
        File("store").apply {
            if(!exists())
                mkdir()
        }
    }

    override fun getStore(name: String): JsonDataStore {
        stores[name]?.apply { return this }

        stores[name] = JsonDataStore(File("store/${name}.json"))
        return stores[name]!!
    }
}