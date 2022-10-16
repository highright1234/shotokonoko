package io.github.highright1234.shotokonoko.storage.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.highright1234.shotokonoko.storage.DataStore
import java.io.File

internal val gson = Gson()

class JsonDataStore(private val file: File) : DataStore {

    private lateinit var json: JsonObject

    init {
        reload()
    }

    override fun <T : Any> set(key: String, value: T) {
        json.add(key, gson.toJsonTree(value))
    }

    override fun increment(key: String, num: Int) {
        set(key, (get(key, Int::class.java) ?: 0) + num)
    }

    override fun <T> get(key: String, type: Class<T>): T? {
        if (!json.has(key))
            return null
        else
            return gson.fromJson(json.get(key), type)
    }

    override fun remove(key: String) {
        json.remove(key)
    }

    override fun save() {
        file.writeText(gson.toJson(json))
    }

    override fun reload() {
        if(!file.exists())
            file.writeText("{}")
        json = JsonParser.parseString(file.readText()).asJsonObject
    }

}