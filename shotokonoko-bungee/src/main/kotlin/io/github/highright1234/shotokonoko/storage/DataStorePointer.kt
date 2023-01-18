package io.github.highright1234.shotokonoko.storage

import kotlin.reflect.KProperty

class DataStorePointer(val dataStore: DataStore, val key: String) {
    inline operator fun <reified T> getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T? {
        return dataStore.get(key, T::class.java)
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Any?
    ) {
        dataStore.set(property.name, value)
    }
}