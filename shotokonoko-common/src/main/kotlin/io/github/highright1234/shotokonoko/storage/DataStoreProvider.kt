package io.github.highright1234.shotokonoko.storage


import com.google.common.cache.CacheBuilder
import io.github.highright1234.shotokonoko.PlatformManager
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

suspend fun <T: DataStore> DataStoreProvider<T>.getStoreAsync(name: String): T =
    withContext(PlatformManager.asyncDispatcher) { getStore(name) }

abstract class DataStoreProvider<T : DataStore> {

    var delayToRemove = 600_000L
//    protected val stores = hashMapOf<String, T>()
//    protected val removingDelayData = hashMapOf<T, MutableDelayData>()
    val cacheOfStores =
        CacheBuilder.newBuilder()
            .expireAfterAccess(delayToRemove, TimeUnit.MILLISECONDS)
            .removalListener<String, T> { removelNotification ->
                removelNotification.key?.let { removeStoreCache(it) }
            }
            .build<String, T>()

    abstract fun getStore(name: String): T

    open fun close() {
        removeAllStoreCaches()
    }

    protected fun registerManager(name: String, dataStore: T) {
        cacheOfStores.get(name) { dataStore }
        AutoSaver.register(dataStore)
    }

    fun removeAllStoreCaches() {
        cacheOfStores.asMap().forEach { (name, _) ->
            removeStoreCache(name)
        }
    }

    fun removeStoreCache(name: String) {
        val store = cacheOfStores.getIfPresent(name) ?: return
        cacheOfStores.invalidate(name)
        AutoSaver.unregister(store)
        store.save()
    }
}