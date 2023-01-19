package io.github.highright1234.shotokonoko.storage.impl.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import org.bson.Document

object MongoDataStoreProvider : DataStoreProvider<MongoDataStore>() {

    var mongoClient: MongoClient? = null
    var database: MongoDatabase? = null
    var collectionName: String? = null
    var collection: MongoCollection<Document>? = null

    fun register(uri: String, databaseName: String, collectionName: String) {
        mongoClient = MongoClients.create(uri)
        mongoClient?.let { client ->
            database = client.getDatabase(databaseName)
        }
        this.collectionName = collectionName
    }

    override fun getStore(name: String): MongoDataStore {
        if (collection == null || database == null || mongoClient == null)
            throw IllegalStateException("MongoDataStoreProvider never been registered")

        collection!!.find(eq("_id", name)).first() ?: kotlin.run {
            Document("_id", name).also { collection!!.insertOne(it) }
        }

        val dataStore = MongoDataStore(name)
        launchStoreRemover(name)

        return dataStore
    }
}