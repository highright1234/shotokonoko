package io.github.highright1234.shotokonoko.storage

interface DataStore {

    fun <T : Any> set(key: String, value: T)

    fun increment(key: String, num: Int)

    fun <T> get(key: String, clazz: Class<T>): T?

    fun remove(key: String)

    fun save()

    fun reload()

}