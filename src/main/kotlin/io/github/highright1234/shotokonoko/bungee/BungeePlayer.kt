package io.github.highright1234.shotokonoko.bungee

data class BungeePlayer(val name: String) {
    companion object {
        val ALL = BungeePlayer("ALL")
    }
}