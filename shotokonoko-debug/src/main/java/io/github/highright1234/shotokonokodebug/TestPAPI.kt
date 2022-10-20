package io.github.highright1234.shotokonokodebug

import io.github.highright1234.shotokonoko.papi.papi

object TestPAPI {
    fun register() = papi {
        expansion("debug") {
            then("faq") {
                requires { player != null }
                executes {
                    "ㅗ"
                }
            }
            then("asdf") {
                executes {
                    "faq"
                }
            }
            argument("value") {
                executes {
                    val value: String by arguments
                    value
                }
                argument("value2") {
                    executes {
                        val value: String by arguments
                        val value2: String by arguments
                        value + value2
                    }
                }
            }
        }
    }
}