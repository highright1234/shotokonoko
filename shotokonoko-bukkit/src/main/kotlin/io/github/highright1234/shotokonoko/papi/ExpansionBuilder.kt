package io.github.highright1234.shotokonoko.papi

import io.github.highright1234.shotokonoko.Shotokonoko
import io.github.highright1234.shotokonoko.papi.node.ExpansionNode
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.named
import org.bukkit.OfflinePlayer

internal object ExpansionBuilder {

    // LibraryLoader 이놈때문에 생긴 클래스로더 문제들 해결한거
    // 지금 여기 클래스로더는 PAPI 이놈을 못쓰기 떄문에
    // 플러그인 클래스로더로 papi expansion 만드는거
    fun register(rootNode: ExpansionNode) {
        val plugin = Shotokonoko.plugin
        val description = plugin.description
        val classLoader = plugin.javaClass.classLoader
        val placeholderExpansionClass = classLoader.loadClass(
            "me.clip.placeholderapi.expansion.PlaceholderExpansion",
        )

        // 가끔 ide에서 defineGetter에 오류내는데 그거 버그임, 넘어가도 잘 작동함
        val instance = ByteBuddy()
            .subclass(placeholderExpansionClass)
            .defineGetter("identifier", rootNode.name)
            .defineGetter("author", description.authors.joinToString())
            .defineGetter("version", description.version)
            .method(named("onRequest"))
            .intercept(MethodDelegation.to(ExpansionInterceptor(rootNode)))
            .make().load(classLoader)
            .loaded.getConstructor().newInstance()

        instance.javaClass.getMethod("register").invoke(instance)
    }

    // capitalize 저거때문임
    @Suppress("DEPRECATION")
    private fun<T> DynamicType.Builder<T>.defineGetter(name: String, value: String) =
        defineMethod("get${name.capitalize()}", String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(value))


    class ExpansionInterceptor(private val rootNode: ExpansionNode) {
        private var indexes = 0
        private var nodeOfIndex = rootNode

        // 실제론 사용됨
        @Suppress("Unused")
        fun onRequest(player: OfflinePlayer?, params: String): String? {
            indexes = 0
            nodeOfIndex = rootNode
            val parameters = params.split("_")
            val context = ExpansionRequestContext(player).apply {
                arguments = arguments + parameters.associateBy { ownerOf(it).name }
                indexes++
            }
            nodeOfIndex.executes ?: error("The end node does not have an execute code.")
            // requires checking
            parentsOf(nodeOfIndex)
                .reversed()
                .find { node -> node.requires.find { check -> !check(context) } != null }
                ?.let {
                    return null
                }
            return nodeOfIndex.executes!!(context)
        }

        private tailrec fun parentsOf(
            node: ExpansionNode,
            nodes: List<ExpansionNode> = listOf()
        ): List<ExpansionNode> {
            val outNodes = nodes + node
            if (node.parent == null) return outNodes
            return parentsOf(node.parent, outNodes)
        }

        private fun ownerOf(value: String): ExpansionNode {
            nodeOfIndex.thenNodes.find { value == it.name }?.let {
                nodeOfIndex = it
                return it
            }
            nodeOfIndex.argumentNode?.let {
                nodeOfIndex = it
                return it
            }
            throw RuntimeException("Not found owner node of value")
        }
    }
}