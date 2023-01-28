package io.github.highright1234.shotokonoko.storage


import io.github.highright1234.shotokonoko.PlatformManager
import io.github.highright1234.shotokonoko.coroutine.MutableDelayData
import io.github.highright1234.shotokonoko.coroutine.mutableDelay
import io.github.highright1234.shotokonoko.launchAsync
import kotlinx.coroutines.withContext

suspend fun <T: DataStore> DataStoreProvider<T>.getStoreAsync(name: String): T =
    withContext(PlatformManager.asyncDispatcher) { getStore(name) }

abstract class DataStoreProvider<T : DataStore> {

    var delayToRemove = 600_000L
    protected val stores = hashMapOf<String, T>()
    protected val removingDelayData = hashMapOf<T, MutableDelayData>()

    abstract fun getStore(name: String): T

    open fun close() {
        removeAllStoreCaches()
    }

    protected fun registerManager(name: String, dataStore: T) {
        stores.getOrPut(name) { dataStore }
        launchStoreRemover(name)
        AutoSaver.register(dataStore)
    }

    fun removeAllStoreCaches() {
        stores.forEach { (name, _) ->
            removeStoreCache(name)
        }
    }

    fun removeStoreCache(name: String) {
        val store = stores[name] ?: return
        stores -= name
        removingDelayData -= store
        AutoSaver.unregister(store)
    }

    protected fun launchStoreRemover(name: String) {
        val store = stores[name] ?: return
        val mutableDelayData = mutableDelay(delayToRemove)
        removingDelayData[store] = mutableDelayData
        launchAsync {
            mutableDelayData.block()
            removeStoreCache(name)
        }
    }
}