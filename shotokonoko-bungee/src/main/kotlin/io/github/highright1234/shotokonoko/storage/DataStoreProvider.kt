package io.github.highright1234.shotokonoko.storage

import com.github.shynixn.mccoroutine.bungeecord.bungeeCordDispatcher
import com.github.shynixn.mccoroutine.bungeecord.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.coroutine.MutableDelayData
import io.github.highright1234.shotokonoko.coroutine.mutableDelay
import kotlinx.coroutines.withContext

suspend fun <T: DataStore> DataStoreProvider<T>.getStoreAsync(name: String): T =
    withContext(plugin.bungeeCordDispatcher) { getStore(name) }

abstract class DataStoreProvider<T : DataStore> {

    var delayToRemove = 600_000L
    protected val stores = hashMapOf<String, T>()
    protected val removingDelayData = hashMapOf<T, MutableDelayData>()

    abstract fun getStore(name: String): T

    protected fun registerManager(name: String, dataStore: T) {
        launchStoreRemover(name, dataStore)
        AutoSaver.register(dataStore)
    }

    internal fun removeAllStoreCaches() {
        stores.forEach { (name, store) ->
            removeStoreCache(name, store)
        }
    }

    private fun removeStoreCache(name: String, store: T) {
        stores -= name
        removingDelayData -= store
        AutoSaver.unregister(store)
    }

    private fun launchStoreRemover(name: String, store: T) {
        val mutableDelayData = mutableDelay(delayToRemove)
        removingDelayData[store] = mutableDelayData
        plugin.launch(plugin.bungeeCordDispatcher) {
            mutableDelayData.block()
            removeStoreCache(name, store)
        }
    }
}