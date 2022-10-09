package io.github.highright1234.shotokonokodebug

import io.github.highright1234.shotokonoko.CoolDownAttribute
import io.github.highright1234.shotokonoko.monun.suspendingExecutes
import io.github.monun.kommand.PluginKommand
import org.bukkit.entity.Player

object TestKommand {
    private val coolDownAttribute = CoolDownAttribute<Player>(5000L)
    fun register(pluginKommand: PluginKommand) {
        pluginKommand.register("test") {
            requires { isPlayer }
            suspendingExecutes {
                coolDownAttribute.withCoolDown(player) {
                    player.sendMessage("${it}ms 쿨타임 있음")
                }
                player.sendMessage("응애")
            }
        }
    }
}