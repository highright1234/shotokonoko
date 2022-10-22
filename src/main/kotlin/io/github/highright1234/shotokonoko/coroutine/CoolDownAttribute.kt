package io.github.highright1234.shotokonoko.coroutine

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import kotlinx.coroutines.cancel
import kotlinx.coroutines.yield
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext

class CoolDownAttribute<T>(private val coolDownTime: Long) {
//    private val coolDownMap = ConcurrentHashMap<T, Long>()
    private val coolDownMap = ConcurrentHashMap<T, MutableDelayData>()
    private val delayedTime get() = System.currentTimeMillis() + coolDownTime
    suspend fun withCoolDown(t: T, runWhenCoolDown: (leftTime: Long) -> Unit = {}) {
        val context = coroutineContext
        coolDownMap[t]?.let {
            if (!it.isActive) return
            runWhenCoolDown(it.timeToRun - System.currentTimeMillis())
            val dispatcher = if (Bukkit.isPrimaryThread()) plugin.minecraftDispatcher else plugin.asyncDispatcher
            plugin.launch(dispatcher) { context.cancel() } // 사실 이거 이렇게 만들어놨었지만 왜 했는지 기억 안남
            yield() // 이거 없으면 작동 안함
        } ?: launchCoolDownJob(t)
    }

    fun coolDown(t: T): Long? {
        val coolTime = coolTimeOf(t)
        coolTime ?: launchCoolDownJob(t)
        coolDownMap[t]?.timeToRun = delayedTime
        return coolTime
    }

    fun coolTimeOf(t: T): Long? = coolDownMap[t]?.takeIf { it.isActive }?.timeToRun

    private fun launchCoolDownJob(t: T) {
        val timeToRun = delayedTime
        val mutableDelayData = MutableDelayData(timeToRun)
        coolDownMap[t] = mutableDelayData
        plugin.launch(plugin.asyncDispatcher) {
            mutableDelayData.block()
            coolDownMap -= t
        }
    }
}