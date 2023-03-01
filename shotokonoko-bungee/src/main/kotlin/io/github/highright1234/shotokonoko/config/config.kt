package io.github.highright1234.shotokonoko.config

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

private val provider: ConfigurationProvider get() = ConfigurationProvider.getProvider(YamlConfiguration::class.java)

// 버킷이랑 헷갈려서 만든거
@Suppress("UnusedReceiverParameter")
fun YamlConfiguration.loadConfiguration(file: File) = provider.load(file)!!

fun Configuration.save(file: File) = provider.save(this, file)

fun Plugin.loadConfig(file: File): Configuration {
    val resourceFile: String = file.toString().removePrefix(dataFolder.toString())
    val config = provider.load(getResourceAsStream(resourceFile))
    if (!file.exists()) {
        config.save(file)
    }
    return config
}

fun Plugin.loadConfig(name: String): Configuration {
    val file = File(plugin.dataFolder, name)
    val config = provider.load(getResourceAsStream(name))
    if (!file.exists()) {
        config.save(file)
    }
    return config
}