package io.github.highright1234.shotokonoko.storage.impl.json

import com.google.common.hash.Hashing
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import java.io.File
import java.nio.charset.StandardCharsets

object JsonDataStoreProvider : DataStoreProvider<JsonDataStore>() {

    init {
        File(plugin.dataFolder, "store").apply {
            if(!exists()) mkdir()
        }
    }

    @Suppress("UnstableApiUsage")
    private val String.sha256 get() = Hashing.sha256().hashString(this, StandardCharsets.UTF_8).toString()

    override fun getStore(name: String): JsonDataStore {
        stores[name]?.let {
            removingDelayData[it]!!.timeToRun = System.currentTimeMillis() + delayToRemove
            return it
        }
        val file = File(plugin.dataFolder, "store/${name.sha256}.json")
        val store = JsonDataStore(file)
        registerManager(name, store)
        stores[name] = store
        return store
    }

}