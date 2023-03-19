package io.github.highright1234.shotokonoko.storage.impl.json

import com.google.common.hash.Hashing
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import java.io.File
import java.nio.charset.StandardCharsets

class JsonDataStoreProvider(val folder: File) : DataStoreProvider<JsonDataStore>() {

    init {
        if (!File(folder, "store").exists()) folder.mkdirs()
    }

    private val String.sha256 get() = Hashing.sha256().hashString(this, StandardCharsets.UTF_8).toString()

    override fun getStore(name: String): JsonDataStore {
        stores[name]?.let {
            removingDelayData[it]?.timeToRun = System.currentTimeMillis() + delayToRemove
            return it
        }
        val storeFolder = File(folder, "store")
        if (!storeFolder.exists()) storeFolder.mkdirs()
        val file = File(storeFolder, "${name.sha256}.json")
        val store = JsonDataStore(file)
        registerManager(name, store)
        return store
    }

}