//package io.github.highright1234.shotokonokodebug
//
//import com.github.shynixn.mccoroutine.bukkit.launch
//import io.github.highright1234.shotokonoko.Shotokonoko.plugin
//import io.github.highright1234.shotokonoko.bungee.BungeePlayer
//import io.github.highright1234.shotokonoko.bungee.BungeeServer
//import io.github.highright1234.shotokonoko.bungee.pluginmessage.BungeeUtil
//import io.github.highright1234.shotokonoko.collections.newPlayerArrayList
//import io.github.highright1234.shotokonoko.coroutine.CooldownAttribute
//import io.github.highright1234.shotokonoko.coroutine.mutableDelay
//import io.github.highright1234.shotokonoko.monun.suspendingExecutes
//import io.github.highright1234.shotokonoko.papi.ppapi
//import io.github.monun.kommand.PluginKommand
//import kotlinx.coroutines.delay
//import net.kyori.adventure.text.Component.text
//import net.kyori.adventure.text.format.TextColor
//import org.bukkit.entity.Player
//import java.lang.ref.WeakReference
//import kotlin.reflect.KProperty1
//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.jvm.isAccessible
//
//// TODO
//object TestKommand {
//    private val coolDownAttribute = CooldownAttribute<Player>(5000L)
//    fun register(pluginKommand: PluginKommand) {
//        pluginKommand.register("test") {
//            requires { isPlayer }
//            suspendingExecutes {
//                coolDownAttribute.withCooldown(player) {
//                    player.sendMessage("${it}ms 쿨타임 있음")
//                    return@suspendingExecutes
//                }
//                val delay = mutableDelay(5000L)
//                player.sendMessage("launched")
//                plugin.launch {
//                    delay(1000L)
//                    player.sendMessage("5000L -> 10000L")
//                    delay.timeToRun += 5000L
//                    player.sendMessage("${delay.isActive}")
//                }
//                plugin.launch {
//                    delay.block()
//                    player.sendMessage("응애")
//                    player.sendMessage("${delay.isActive}")
//                }
//            }
//            then("garbage_check") {
//                suspendingExecutes {
//                    newPlayerArrayList()
//                    val clazz = Class.forName(
//                        "io.github.highright1234.shotokonoko.collections.PlayerGCProcessor"
//                    ).kotlin
//                    @Suppress("UNCHECKED_CAST")
//                    val property = clazz.memberProperties.first { it.name == "targetCollections" }
//                            as KProperty1<Any, MutableList<WeakReference<MutableCollection<Player>>>>
//                    val value = property.apply { isAccessible = true }.call().map { it.get() }
////                    val value = property.javaField!!.apply { isAccessible = true }.get(clazz.objectInstance!!)
//                    player.sendMessage(value.toString())
//                }
//            }
//            then("gc") {
//                suspendingExecutes {
//                    System.gc()
//                    player.sendMessage("gc ran")
//                }
//            }
//            then("papi_checker") {
//                suspendingExecutes {
//                    val firstResult = "%debug_fdsa_fdsa%".let(::ppapi)
//                    val secondResult = "%debug_asdf%".let(::ppapi)
//                    val thirdResult = "%debug_fdsa%".let(::ppapi)
//                    val fourthResult = "%debug_faq%".let(::ppapi)
//                    val fifthResult = ppapi("%debug_faq%", player)
//
//                    val componentResult = ppapi(
//                        text("%debug_faq%나 머겅")
//                            .color(TextColor.color(0xFF66CC)), player // pink
//                    )
//                    if (
//                        firstResult == "fdsafdsa" &&
//                        secondResult == "faq" &&
//                        thirdResult == "fdsa" &&
//                        fourthResult == "%debug_faq%" &&
//                        fifthResult == "ㅗ"
//                    ) {
//                        player.sendMessage("아주 좋소~")
//                    } else {
//                        player.sendMessage("버근가")
//                    }
//                    listOf(
//                        firstResult,
//                        secondResult,
//                        thirdResult,
//                        fourthResult,
//                        fifthResult
//                    ).forEach(player::sendMessage)
//                    player.sendMessage(componentResult)
//                }
//            }
//            then("bungee") {
//                requires { BungeeUtil.isBungee }
//                then("connect") {
//                    then("name" to string()) {
//                        executes {
//                            BungeeUtil.connect(player, BungeeServer(it["name"]))
//                        }
//                    }
//                }
//                then("this_server") {
//                    suspendingExecutes {
//                        BungeeUtil.getServer().await()
//                            .let { "name: ${it.name}" }
//                            .let(player::sendMessage)
//                    }
//                }
//                then("servers") {
//                    suspendingExecutes {
//                        BungeeUtil.getServers().await()
//                            .joinToString { it.name }
//                            .let { "servers: $it" }
//                            .let(player::sendMessage)
//                    }
//                }
//                then("message") {
//                    suspendingExecutes {
//                        BungeeUtil.message(
//                            BungeePlayer(player.name),
//                            text("뀨").color(TextColor.color(0xFF66CC))
//                        )
//                    }
//                }
//            }
//        }
//    }
//}