package io.github.highright1234.shotokonoko.debug

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.monun.init
import io.github.monun.tap.fake.FakeEntityServer
import kotlinx.coroutines.delay
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

private object DebugUtil {
    lateinit var fakeEntityServer: FakeEntityServer
    private val ops: List<Player> get() = Bukkit.getOnlinePlayers().filter { it.isOp }

    var removeDelay: Long = 1000

    fun enable() {
        // TODO block-display, bouding-box, location particle
        fakeEntityServer = FakeEntityServer.create(Shotokonoko.plugin)
        fakeEntityServer.init { it.isOp }
    }

    fun blockLocation(location: Location, keepWhen: (() -> Boolean)? = null) {
        val fakeEntity = fakeEntityServer.spawnFallingBlock(location, Material.GLASS.createBlockData())

        fakeEntity.updateMetadata {
            isGlowing = true
        }

        if (keepWhen == null) {
            plugin.launch {
                delay(removeDelay)
                fakeEntity.remove()
            }
        } else {
            plugin.launch {
                while (keepWhen()) {
                    delay(500L)
                }
                fakeEntity.remove()
            }
        }
    }

    fun point(location: Location, keepWhen: (() -> Boolean)? = null) {
        require(location.world != null) { "world in null!" }
        val data = DustOptions(Color.RED, 1f)
        plugin.launch {
            ops.forEach { player ->
                player.spawnParticle(Particle.REDSTONE, location, 10, data)
            }
            keepWhen?.let {
                while (keepWhen()) {
                    delay(500L)
                }
            }
        }
    }

    fun boundingBox(boundingBox: BoundingBox, world: World, keepWhen: (() -> Boolean)? = null) {
        keepWhen?.let {
            plugin.launch {
                while (keepWhen()) {
                    delay(500L)
                }
            }
        }
        TODO()
    }

    fun glowEntities(entities: List<Entity>) {

    }
}