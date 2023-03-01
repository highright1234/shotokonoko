package io.github.highright1234.shotokonoko.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KProperty

suspend fun DataStore.saveAsync() = withContext(Dispatchers.IO) { save() }
suspend fun DataStore.reloadAsync() = withContext(Dispatchers.IO) { reload() }

// 이거 데이터 async로 받는것도 만들고싶은데 귀찮
// 만들어서 pr 누가 해줬으면

interface DataStore {

    fun <T : Any> set(key: String, value: T?)

    fun <T> get(key: String, clazz: Class<T>): T?

    fun remove(key: String)

    fun save()

    fun reload()

    val keys: List<String>

}

inline operator fun <reified T> DataStore.getValue(
    thisRef: Any?,
    property: KProperty<*>
): T? {
    return get(property.name, T::class.java)
}

operator fun DataStore.setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: Any?
) {
    set(property.name, value)
}


operator fun DataStore.get(key: String) = DataStorePointer(this, key)
