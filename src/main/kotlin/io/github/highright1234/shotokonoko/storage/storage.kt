package io.github.highright1234.shotokonoko.storage

import io.github.highright1234.shotokonoko.storage.impl.JsonDataStoreProvider
import kotlin.reflect.KProperty

fun getDataStore(name: String)
    = JsonDataStoreProvider.getDataStore(name)

fun <T : DataStore> DataStoreProvider<T>.getDataStore(name: String): T
    = getStore(name)

inline operator fun <reified T> DataStore.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T? {
    return get(property.name, T::class.java)
}