package io.github.highright1234.shotokonokodebug

import io.github.highright1234.shotokonoko.storage.getDataStore
import io.github.highright1234.shotokonoko.storage.getValue
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue

object StorageTestKommand {
    fun register(pluginKommand: PluginKommand) {
        pluginKommand.register("storage") {
            requires { isPlayer }
            then("set") {
                then("key" to string()) {
                    then("value" to int()) {
                        executes { ctx ->
                            val key: String by ctx
                            val value: Int by ctx
                            val store = getDataStore(player.uniqueId.toString())
                            store.set(key, value)
                        }
                    }
                }
            }
            then("inc") {
                then("key" to string()) {
                    executes { ctx ->
                        val key: String by ctx
                        val store = getDataStore(player.uniqueId.toString())
                        store.increment(key, 1)
                    }
                }
            }
            then("get") {
                then("key" to string()) {
                    executes { ctx ->
                        val key: String by ctx
                        val store = getDataStore(player.uniqueId.toString())
                        player.sendMessage("${store.get(key, Int::class.java)}")
                    }
                }
            }
            then("getBy") {
                executes { ctx ->
                    val store = getDataStore(player.uniqueId.toString())
                    val test: Int? by store
                    player.sendMessage("$test")
                }
            }
            then("save") {
                executes { ctx ->
                    val store = getDataStore(player.uniqueId.toString())
                    store.save()
                }
            }
        }
    }
}