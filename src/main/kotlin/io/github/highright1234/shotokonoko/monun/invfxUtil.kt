package io.github.highright1234.shotokonoko.monun

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.monun.invfx.frame.InvSlot
import org.bukkit.event.inventory.InventoryClickEvent

fun InvSlot.onSuspendingClick(onClick: suspend (InventoryClickEvent) -> Unit) {
    onClick {
        plugin.launch {
            onClick(it)
        }
    }
}