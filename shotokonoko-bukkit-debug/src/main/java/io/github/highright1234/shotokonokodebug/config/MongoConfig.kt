package io.github.highright1234.shotokonokodebug.config

import io.github.highright1234.shotokonoko.config.ConfigFrame
import io.github.monun.tap.config.Config

object MongoConfig: ConfigFrame() {
    @Config
    var enable: Boolean = false
    @Config
    var address: String = "localhost"
    @Config
    var port: Int = 27017

    @Config
    var database: String = "shotokonoko"

    @Config
    var collection: String = "players"

    object User {

        @Config
        var username: String = "root"
        @Config
        var password: String = "root"
        @Config
        var database: String = "admin"

    }
}