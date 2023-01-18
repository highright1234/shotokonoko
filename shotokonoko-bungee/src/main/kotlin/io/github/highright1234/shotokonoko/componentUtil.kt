package io.github.highright1234.shotokonoko

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

fun text(string: String) = TextComponent(string)

operator fun BaseComponent.plus(component: BaseComponent) = duplicate()!!.apply { addExtra(component) }
operator fun BaseComponent.plus(string: String) = this + text(string)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun String.plus(component: BaseComponent) = text(this) + component
//
//val Component.string get() = PlainTextComponentSerializer.plainText().serialize(this)
//val Component.richString get() = MiniMessage.miniMessage().serialize(this)
//val String.rich get() = MiniMessage.miniMessage().deserialize(this)