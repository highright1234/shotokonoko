package io.github.highright1234.shotokonoko.storage

import io.github.highright1234.shotokonoko.launchAsync
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

object AutoSaver {
    var isEnabled = true
        set(value) {
            field = value
            if (value != field && !value) {
                savers.forEach { (dataStore, _) ->
                    unregister(dataStore)
                }
            }
        }
    var autosaveDelay = 60_000L
    private val savers = ConcurrentHashMap<DataStore, Job>()
    fun register(dataStore: DataStore) {
        if (!isEnabled) return
        savers[dataStore]?.let { return }
        savers[dataStore] = launchAsync {
            while (true) {
                delay(autosaveDelay)
                dataStore.saveAsync()
            }
        }
    }

    fun unregister(dataStore: DataStore) {
        savers[dataStore]?.cancel()
        savers -= dataStore
    }
}