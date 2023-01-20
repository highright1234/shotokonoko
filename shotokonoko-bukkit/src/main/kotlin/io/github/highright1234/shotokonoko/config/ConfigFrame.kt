package io.github.highright1234.shotokonoko.config

import io.github.highright1234.shotokonoko.Shotokonoko
import io.github.highright1234.shotokonoko.monun.load
import io.github.monun.tap.config.ConfigSupport
import java.io.File

abstract class ConfigFrame {

    private var file: File? = null
    private var path: String? = null

    fun load(file: File) {
        ConfigSupport.load(this, file)
        this.file = file
    }

    fun load(path: String) {
        ConfigSupport.load(this, path)
        this.path = path
    }

    fun save() {
        val configFile =
            file ?:
            path?.let { File(Shotokonoko.plugin.dataFolder, it) } ?:
            throw IllegalStateException("Never loaded before")
        ConfigSupport.compute(this, configFile)
    }
}