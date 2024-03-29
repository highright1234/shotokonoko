package io.github.highright1234.shotokonoko.coroutine

import io.github.highright1234.shotokonoko.launchAsync
import java.util.concurrent.ConcurrentHashMap

class CooldownAttribute<T>(private val coolDownTime: Long) {
//    private val coolDownMap = ConcurrentHashMap<T, Long>()
    private val coolDownMap = ConcurrentHashMap<T, MutableDelayData>()
    private val delayedTime get() = System.currentTimeMillis() + coolDownTime

    inline fun withCooldown(t: T, runWhenCooling: (leftTime: Long) -> Unit): Long? {
        return cooldownOf(t)?.also {
            runWhenCooling(it - System.currentTimeMillis())
        } ?: run { cooldown(t) }
    }

    fun cooldown(t: T): Long? {
        val coolTime = cooldownOf(t)
        coolTime ?: launchCooldownJob(t)
        coolDownMap[t]?.timeToRun = delayedTime
        return coolTime
    }

    fun cooldownOf(t: T): Long? = coolDownMap[t]?.takeIf { it.isActive }?.timeToRun

    private fun launchCooldownJob(t: T) {
        val timeToRun = delayedTime
        val mutableDelayData = MutableDelayData(timeToRun)
        coolDownMap[t] = mutableDelayData
        launchAsync {
            mutableDelayData.block()
            coolDownMap -= t
        }
    }
}