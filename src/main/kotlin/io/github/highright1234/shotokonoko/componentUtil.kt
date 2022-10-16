package io.github.highright1234.shotokonoko

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

operator fun Component.plus(component: Component) = append(component)
operator fun Component.plus(string: String) = this + text(string)

val Component.string get() = PlainTextComponentSerializer.plainText().serialize(this)
val Component.richString get() = MiniMessage.miniMessage().serialize(this)
val String.rich get() = MiniMessage.miniMessage().deserialize(this)