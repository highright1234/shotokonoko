package io.github.highright1234.shotokonoko.papi.node

import io.github.highright1234.shotokonoko.papi.ExpansionRequestContext

open class ExpansionNode(
    internal val parent: ExpansionNode?,
    internal val name: String
) {

    internal val thenNodes = mutableListOf<ExpansionNode>()
    internal var argumentNode: ArgumentNode? = null
    internal var executes: ((ExpansionRequestContext) -> String?)? = null
    internal val requires: MutableList<((ExpansionRequestContext) -> Boolean)> = mutableListOf({ true })

    fun requires(block: ExpansionRequestContext.() -> Boolean) {
        requires += block
    }

    fun then(name: String, block: ExpansionNode.() -> Unit) {
        thenNodes.find { it.name == name }?.let { error("No nodes should have same name") }
        thenNodes += ExpansionNode(this, name).apply(block)
    }

    fun argument(name: String, block: ArgumentNode.() -> Unit) {
        argumentNodes.find { it.name == name }?.let { error("No argument nodes should have same name") }
        argumentNode = ArgumentNode(this, name).apply(block)
    }

    fun executes(block: ExpansionRequestContext.() -> String?) {
        executes = block
    }





    private val argumentNodes get() = argumentNodes(this)
    private tailrec fun argumentNodes(node: ExpansionNode, nodes: List<ArgumentNode> = listOf()): List<ArgumentNode> {
        val outNodes = if (node is ArgumentNode) nodes + node else nodes
        if (node.parent == null) return outNodes
        return argumentNodes(node.parent, outNodes)
    }
}

