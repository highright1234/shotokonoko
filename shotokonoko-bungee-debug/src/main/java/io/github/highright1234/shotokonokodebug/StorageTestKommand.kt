//package io.github.highright1234.shotokonokodebug
//
//import io.github.highright1234.shotokonoko.monun.suspendingExecutes
//import io.github.highright1234.shotokonoko.storage.*
//import io.github.monun.kommand.PluginKommand
//import io.github.monun.kommand.getValue
//import org.bukkit.Location
//
//// TODO
//object StorageTestKommand {
//    fun register(pluginKommand: PluginKommand) {
//        pluginKommand.register("storage") {
//            requires { isPlayer }
//            then("set") {
//                then("key" to string()) {
//                    then("location") {
//                        suspendingExecutes { ctx ->
//                            val key: String by ctx
//                            val store = getDataStoreAsync(player)
//                            store.set(key, player.location)
//                        }
//                    }
//                    then("value" to int()) {
//                        suspendingExecutes { ctx ->
//                            val key: String by ctx
//                            val value: Int by ctx
//                            val store = getDataStoreAsync(player)
//                            store.set(key, value)
//                        }
//                    }
//                }
//            }
//            then("inc") {
//                then("key" to string()) {
//                    suspendingExecutes { ctx ->
//                        val key: String by ctx
//                        val store = getDataStoreAsync(player)
//                        var test: Int? by store[key]
//                        test = test!! + 1
////                        store.increment(key, 1)
//                    }
//                }
//            }
//            then("get") {
//                then("key" to string()) {
//                    suspendingExecutes { ctx ->
//                        val key: String by ctx
//                        val store = getDataStoreAsync(player)
//                        player.sendMessage("${store.get(key, Int::class.java)}")
//                    }
//                    then("location") {
//                        suspendingExecutes { ctx ->
//                            val key: String by ctx
//                            val store = getDataStoreAsync(player)
//                            player.sendMessage("${store.get(key, Location::class.java)}")
//                        }
//                    }
//                }
//            }
//            then("getBy") {
//                suspendingExecutes {
//                    val store = getDataStoreAsync(player)
//                    val test: Int? by store
//                    player.sendMessage("$test")
//                }
//            }
//            then("save") {
//                suspendingExecutes {
//                    val store = getDataStoreAsync(player)
//                    store.saveAsync()
//                }
//            }
//            then("reload") {
//                suspendingExecutes {
//                    val store = getDataStoreAsync(player)
//                    store.reloadAsync()
//                }
//            }
//        }
//    }
//}
