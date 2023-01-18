package io.github.highright1234.shotokonoko.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

suspend inline fun <T> withTimeoutResult(timeMillis: Long, crossinline block: suspend CoroutineScope.() -> T): Result<T> =
    try {
        withTimeout(timeMillis) {
            Result.success(block())
        }
    } catch(e: TimeoutCancellationException) {
        Result.failure<T>(e)
    }
