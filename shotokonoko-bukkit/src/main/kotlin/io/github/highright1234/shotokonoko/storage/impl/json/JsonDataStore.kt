package io.github.highright1234.shotokonoko.storage.impl.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.highright1234.shotokonoko.storage.DataStore
import io.github.highright1234.shotokonoko.storage.impl.ConfigurationGsonSerializer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.File

internal val gson = GsonBuilder()
    .registerTypeHierarchyAdapter(ConfigurationSerializable::class.java, ConfigurationGsonSerializer)
    .create()

class JsonDataStore(private val file: File) : DataStore {

    private lateinit var json: JsonObject

    init {
        reload()
    }

    override fun <T : Any> set(key: String, value: T?) {
        json.add(key, gson.toJsonTree(value))
    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        return gson.takeIf { json.has(key) }?.fromJson(json.get(key), clazz)
    }

    override fun remove(key: String) {
        json.remove(key)
    }

    override fun save() {
        file.writeText(gson.toJson(json))
    }

    override fun reload() {
        if(!file.exists()) file.writeText("{}")
        json = JsonParser.parseString(file.readText()).asJsonObject
    }

}