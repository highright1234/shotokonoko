package io.github.highright1234.shotokonoko

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.File
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job = PlatformManager.launchAsync(block)

object PlatformManager {

    private val platformManager : Any by lazy {
        Class.forName(PlatformManager::class.java.packageName + ".PlatformManagerImpl")
            .kotlin.objectInstance!!
    }

    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
        return platformManager.javaClass.kotlin
            .memberFunctions
            .first { it.name == "launchAsync" }
            .call(platformManager, block) as Job
    }
    val asyncDispatcher: CoroutineContext by Getter

    val pluginLoader: Any by Getter

    val logger: Logger get() = plugin.get("logger")
    val plugin: Any by Getter
    val dataFolder: File get() = plugin.get("dataFolder")


    private fun <T> Any.get(name: String): T {
        var clazz = this.javaClass
        while (clazz != Any::class.java) {
            @Suppress("UNCHECKED_CAST")
            clazz.kotlin
                .memberProperties
                .find { it.name == name }
                ?.let {
                    return it.apply { isAccessible = true }.get(this@get) as T
                } ?: run {
                clazz = clazz.superclass
            }
        }
        error("Not found $name on ${clazz.name}")
    }

//    private fun <T> Any.call(name: String, vararg arguments : Any?): T {
//        @Suppress("UNCHECKED_CAST")
//        return javaClass.kotlin
//            .memberFunctions
//            .first { it.name == name }
//            .call(arguments) as T
//    }

    private object Getter {
        operator fun <T> getValue(thisRef: Any?, property: KProperty<*>) : T {
            @Suppress("UNCHECKED_CAST")
            return platformManager.javaClass.kotlin
                .memberProperties.first { it.name == property.name }
                .get(platformManager) as T
        }
    }
}