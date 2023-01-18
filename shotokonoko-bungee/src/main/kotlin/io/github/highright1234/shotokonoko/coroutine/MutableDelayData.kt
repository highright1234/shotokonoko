package io.github.highright1234.shotokonoko.coroutine

import com.github.shynixn.mccoroutine.bungeecord.bungeeCordDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import kotlinx.coroutines.withContext

fun mutableDelay(timeMillis: Long) = MutableDelayData(System.currentTimeMillis() + timeMillis)

class MutableDelayData(whenEnd: Long) {
    // time millis
    var timeToRun = whenEnd
    val isActive: Boolean get() = System.currentTimeMillis() <= timeToRun

    fun delay(timeMillis: Long) { timeToRun += timeMillis }

    suspend fun block() {
        withContext(plugin.bungeeCordDispatcher) {
            while (System.currentTimeMillis() <= timeToRun) {
                kotlinx.coroutines.delay(1)
            }
            // 그냥 시간 뻐기기
        }
    }
}