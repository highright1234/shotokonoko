package io.github.highright1234.shotokonoko.papi.node

import io.github.highright1234.shotokonoko.papi.ExpansionBuilder

object PapiNode {
    fun expansion(identifier: String, block: ExpansionNode.() -> Unit) {
        val node = ExpansionNode(null, identifier).apply(block)
        ExpansionBuilder.register(node)
    }
}
