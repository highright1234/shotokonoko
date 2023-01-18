package io.github.highright1234.shotokonoko.monun

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.node.KommandNode

fun KommandNode.suspendingExecutes(executes: suspend KommandSource.(KommandContext) -> Unit) {
    executes { kommandContext ->
        plugin.launch {
            executes.invoke(this@executes, kommandContext)
        }
    }
}
