package io.github.highright1234.shotokonoko.storage.impl.mongodb

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import io.github.highright1234.shotokonoko.storage.DataStoreProvider
import io.github.highright1234.shotokonoko.storage.Storage
import org.bson.Document
import java.net.InetSocketAddress

object MongoDataStoreProvider : DataStoreProvider<MongoDataStore>() {

    var mongoClient: MongoClient? = null
    var database: MongoDatabase? = null
    var collection: MongoCollection<Document>? = null

    fun register(
        address: InetSocketAddress, credential: MongoCredential,
        databaseName: String, collectionName: String
    ) {
        mongoClient = MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings {
                    it.hosts(
                        listOf(ServerAddress(address))
                    )
                }
                .credential(credential)
                .build())


        database = mongoClient!!.getDatabase(databaseName)
        collection = database!!.getCollection(collectionName)

        Storage.dataStoreProviders += this
        Storage.defaultProvider = this
    }

    override fun getStore(name: String): MongoDataStore {
        if (collection == null || database == null || mongoClient == null)
            throw IllegalStateException("MongoDataStoreProvider never been registered")

        collection!!.find(eq("_id", name)).first() ?: kotlin.run {
            Document("_id", name).also { collection!!.insertOne(it) }
        }

        val dataStore = MongoDataStore(name)
        stores[name] = dataStore
        launchStoreRemover(name)

        return dataStore
    }
}