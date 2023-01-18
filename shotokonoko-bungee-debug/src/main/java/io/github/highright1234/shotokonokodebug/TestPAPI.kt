//package io.github.highright1234.shotokonokodebug
//
//import io.github.highright1234.shotokonoko.papi.papi
//import io.github.highright1234.shotokonoko.storage.getDataStore
//
//// TODO
//object TestPAPI {
//    fun register() = papi {
//        expansion("dataStore") {
//            requires { player != null }
//            argument("key") {
//                executes {
//                    getDataStore(player!!).get(arguments["key"]!!, Any::class.java)?.let {
//                        it::class.java.simpleName
//                    }
//                }
//            }
//        }
//        expansion("debug") {
//            then("faq") {
//                requires { player != null }
//                executes {
//                    "ㅗ"
//                }
//            }
//            then("asdf") {
//                executes {
//                    "faq"
//                }
//            }
//            argument("value") {
//                executes {
//                    val value: String by arguments
//                    value
//                }
//                argument("value2") {
//                    executes {
//                        val value: String by arguments
//                        val value2: String by arguments
//                        value + value2
//                    }
//                }
//            }
//        }
//    }
//}