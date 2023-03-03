package io.github.highright1234.shotokonoko.config

import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

private val provider: ConfigurationProvider get() = ConfigurationProvider.getProvider(YamlConfiguration::class.java)

// 버킷이랑 헷갈려서 만든거
fun YamlConfiguration.loadConfiguration(file: File) = provider.load(file)!!

private fun loadConfiguration(file: File) = provider.load(file)!!

fun Configuration.save(file: File) = provider.save(this, file)

fun Plugin.loadConfig(file: File): Configuration {
    if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
    val resourceFile: String = file.toString().removePrefix(dataFolder.toString())

    lateinit var config: Configuration
    if (!file.exists()) {
        config = provider.load(getResourceAsStream(resourceFile))
        config.save(file)
    } else {
        config = loadConfiguration(file)
    }

    return config
}

fun Plugin.loadConfig(name: String): Configuration {
    if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
    val file = File(plugin.dataFolder, name)
    lateinit var config: Configuration
    if (!file.exists()) {
        config = provider.load(getResourceAsStream(name))
        config.save(file)
    } else {
        config = loadConfiguration(file)
    }
    return config
}