package io.github.highright1234.shotokonoko.coroutine

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext

class CoolDownAttribute<T>(private val coolDownTime: Long) {
    private val coolDownMap = ConcurrentHashMap<T, Long>()
    suspend fun withCoolDown(t: T, runWhenCoolDown: (leftTime: Long) -> Unit = {}) {
        val context = coroutineContext
        coolDownMap[t]?.let {
            if (it - System.currentTimeMillis() < 0) return
            runWhenCoolDown(it - System.currentTimeMillis())
            val dispatcher = if (Bukkit.isPrimaryThread()) plugin.minecraftDispatcher else plugin.asyncDispatcher
            plugin.launch(dispatcher) { context.cancel() }
            yield()
        } ?: launchCoolDownJob(t)
    }

    fun coolDown(t: T): Long? {
        val coolTime = coolTimeOf(t)
        coolTime ?: launchCoolDownJob(t)
        return coolTime
    }

    fun coolTimeOf(t: T): Long? = coolDownMap[t]?.takeIf { it - System.currentTimeMillis() < 0 }

    private fun launchCoolDownJob(t: T) {
        val time = System.currentTimeMillis() + coolDownTime
        coolDownMap[t] = time
        plugin.launch {
            delay(coolDownTime)
            coolDownMap -= t
        }
    }
}