package io.github.highright1234.shotokonoko.storage.impl.json

import com.google.common.hash.Hashing
import io.github.highright1234.shotokonoko.PlatformManager
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import java.io.File
import java.nio.charset.StandardCharsets

object JsonDataStoreProvider : DataStoreProvider<JsonDataStore>() {

    init {
        File(PlatformManager.dataFolder, "store").apply {
            if(!exists()) mkdir()
        }
    }

    private val String.sha256 get() = Hashing.sha256().hashString(this, StandardCharsets.UTF_8).toString()

    override fun getStore(name: String): JsonDataStore {
        stores[name]?.let {
            removingDelayData[it]?.timeToRun = System.currentTimeMillis() + delayToRemove
            return it
        }
        val file = File(PlatformManager.dataFolder, "store/${name.sha256}.json")
        val store = JsonDataStore(file)
        registerManager(name, store)
        return store
    }

}