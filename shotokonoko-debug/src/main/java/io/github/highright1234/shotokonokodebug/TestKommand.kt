package io.github.highright1234.shotokonokodebug

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.collections.newPlayerArrayList
import io.github.highright1234.shotokonoko.coroutine.CoolDownAttribute
import io.github.highright1234.shotokonoko.coroutine.mutableDelayData
import io.github.highright1234.shotokonoko.monun.suspendingExecutes
import io.github.monun.kommand.PluginKommand
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object TestKommand {
    private val coolDownAttribute = CoolDownAttribute<Player>(5000L)
    fun register(pluginKommand: PluginKommand) {
        pluginKommand.register("test") {
            requires { isPlayer }
            suspendingExecutes {
                coolDownAttribute.withCoolDown(player) {
                    player.sendMessage("${it}ms 쿨타임 있음")
                }
                val delay = mutableDelayData(5000L)
                player.sendMessage("launched")
                plugin.launch {
                    delay(1000L)
                    player.sendMessage("5000L -> 10000L")
                    delay.timeToRun += 5000L
                    player.sendMessage("${delay.isActive}")
                }
                plugin.launch {
                    delay.block()
                    player.sendMessage("응애")
                    player.sendMessage("${delay.isActive}")
                }
            }
            then("garbage_check") {
                suspendingExecutes {
                    val test = newPlayerArrayList()
                    val clazz = Class.forName(
                        "io.github.highright1234.shotokonoko.collections.PlayerGCProcessor"
                    ).kotlin
                    @Suppress("UNCHECKED_CAST")
                    val property = clazz.memberProperties.first { it.name == "targetCollections" }
                            as KProperty1<Any, MutableList<WeakReference<MutableCollection<Player>>>>
                    val value = property.apply { isAccessible = true }.call().map { it.get() }
//                    val value = property.javaField!!.apply { isAccessible = true }.get(clazz.objectInstance!!)
                    player.sendMessage(value.toString())
                }
            }
            then("gc") {
                executes {
                    System.gc()
                    player.sendMessage("gc ran")
                }
            }
        }
    }
}