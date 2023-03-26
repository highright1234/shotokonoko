package io.github.highright1234.shotokonoko.monun

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.monun.invfx.frame.InvList
import io.github.monun.invfx.frame.InvSlot
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

fun InvSlot.onSuspendingClick(onClick: suspend (InventoryClickEvent) -> Unit) {
    onClick {
        plugin.launch {
            onClick(it)
        }
    }
}

fun <T> InvList<T>.onSuspendingClickItem(onClickItem: suspend (x: Int, y: Int, item: Pair<T, ItemStack>, event: InventoryClickEvent) -> Unit) {
    onClickItem { x, y, item, event ->
        plugin.launch {
            onClickItem(x, y, item, event)
        }
    }
}

fun <T> InvList<T>.onSuspendingUpdate(onUpdate: suspend (list: List<Pair<T, ItemStack>>, index: Int) -> Unit) {
    onUpdate { list, index ->
        plugin.launch {
            onUpdate(list, index)
        }
    }
}
