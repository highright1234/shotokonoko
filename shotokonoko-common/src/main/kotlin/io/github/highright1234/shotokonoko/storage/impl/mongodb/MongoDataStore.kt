package io.github.highright1234.shotokonoko.storage.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.github.highright1234.shotokonoko.storage.DataStore
import io.github.highright1234.shotokonoko.storage.impl.json.JsonDataStore
import org.bson.Document
import org.bson.conversions.Bson

class MongoDataStore internal constructor(val id: String) : DataStore {

    private val gson get() = JsonDataStore.gson

    val collection: MongoCollection<Document> get() = MongoDataStoreProvider.collection!!
    val filter: Bson = Filters.eq("_id", id)

    override fun <T : Any> set(key: String, value: T?) {
        val jsonElement = gson.toJsonTree(value)
        // 현재 재대로된 처리는 못하고있음
        // https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/data-formats/documents/
        // org.bson.types.Binary, org.bson.types.ObjectId, org.bson.Document
        // 이거 처리 관련 생각 TODO
        val bsonValue: Any? = if (!jsonElement.isJsonPrimitive) {
            Document.parse(gson.toJsonTree(value).toString())
        } else value
        val update = bsonValue?.let { Updates.set(key, it) } ?: Updates.unset(key)
        collection.updateOne(filter, update)
    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        return collection.find(filter).first()!![key]?.let {

            if (it is Document) return gson.fromJson(it.toJson().also(::println), clazz)
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

    override fun remove(key: String) {
        collection.updateOne(filter, Updates.unset(key))
    }

    override fun save() {}

    override fun reload() {}
}