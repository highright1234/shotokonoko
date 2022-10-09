package io.github.highright1234.shotokonoko

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text

operator fun Component.plus(component: Component) = append(component)
operator fun Component.plus(string: String) = this + text(string)