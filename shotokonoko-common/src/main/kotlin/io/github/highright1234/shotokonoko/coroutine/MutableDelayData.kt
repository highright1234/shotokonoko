package io.github.highright1234.shotokonoko.coroutine

import io.github.highright1234.shotokonoko.PlatformManager
import kotlinx.coroutines.withContext

fun mutableDelay(timeMillis: Long) = MutableDelayData(System.currentTimeMillis() + timeMillis)

class MutableDelayData(whenEnd: Long) {
    // time millis
    var timeToRun = whenEnd
    val isActive: Boolean get() = System.currentTimeMillis() <= timeToRun

    fun delay(timeMillis: Long) { timeToRun += timeMillis }

    suspend fun block() {
        withContext(PlatformManager.asyncDispatcher) {
            while (System.currentTimeMillis() <= timeToRun) {
                kotlinx.coroutines.delay(1)
            }
            // 그냥 시간 뻐기기
        }
    }
}