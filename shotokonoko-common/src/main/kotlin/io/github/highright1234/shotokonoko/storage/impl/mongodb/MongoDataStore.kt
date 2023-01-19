package io.github.highright1234.shotokonoko.storage.impl.mongodb

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.github.highright1234.shotokonoko.storage.DataStore
import org.bson.Document
import org.bson.conversions.Bson

class MongoDataStore  internal constructor(val id: String) : DataStore {

    val collection: MongoCollection<Document> get() = MongoDataStoreProvider.collection!!
    val filter: Bson = Filters.eq("_id", id)

    override fun <T : Any> set(key: String, value: T?) {
        collection.updateOne(filter, Updates.set(key, value))
    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        return collection.find(filter).first()!!.get(key, clazz)
    }

    override fun remove(key: String) {
        collection.updateOne(filter, Updates.unset(key))
    }

    override fun save() {}

    override fun reload() {}
}