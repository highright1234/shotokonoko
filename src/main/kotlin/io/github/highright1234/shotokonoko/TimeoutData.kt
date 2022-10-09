package io.github.highright1234.shotokonoko

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.Bukkit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

suspend fun withTimeOut(delayTime: Long, whenTimedOut: suspend () -> Unit) =
    TimeoutData.newTimeoutData(
        delayTime,
        whenTimedOut
    )

class TimeoutData private constructor(
    private val parent: CoroutineContext,
    delay: Long,
    whenTimedOut: suspend () -> Unit
) {
    private val cancelJob: Job
    init {
        val dispatcher = if (Bukkit.isPrimaryThread()) plugin.minecraftDispatcher else plugin.asyncDispatcher
        cancelJob = plugin.launch(dispatcher) {
            delay(delay)
            if (!parent.isActive) return@launch
            parent.cancel()
            whenTimedOut()
        }
    }

    companion object {
        suspend fun newTimeoutData(delay: Long, whenTimedOut: suspend () -> Unit) =
            TimeoutData(
                coroutineContext,
                delay,
                whenTimedOut
            )
    }

    fun complete() {
        cancelJob.cancel()
    }
}