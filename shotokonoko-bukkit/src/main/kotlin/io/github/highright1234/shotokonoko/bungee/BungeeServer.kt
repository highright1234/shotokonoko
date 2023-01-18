package io.github.highright1234.shotokonoko.bungee

data class BungeeServer(val name: String) {
    companion object {
        val ALL = BungeeServer("ALL")
    }
}
