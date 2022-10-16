package io.github.highright1234.shotokonoko.storage

interface DataStoreProvider<T : DataStore> {

    fun getStore(name: String): T

}