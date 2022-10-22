package io.github.highright1234.shotokonoko.storage.impl

import com.google.gson.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter
import java.lang.reflect.Type

internal object ConfigurationGsonSerializer:
    JsonSerializer<ConfigurationSerializable>,
    JsonDeserializer<ConfigurationSerializable> {

    private val yaml = Yaml()
    override fun serialize(
        src: ConfigurationSerializable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        src ?: return JsonObject()
        val yamlString = YamlConfiguration().apply { set("dummy", src) }.saveToString()
        val loadedYaml: Any = yaml.load(yamlString)
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJsonTree(loadedYaml, LinkedHashMap::class.java).asJsonObject["dummy"]
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext?
    ): ConfigurationSerializable {
        val data = yaml.load<Map<String, Any>>(json.asJsonObject.toString())
        val writer = StringWriter()
        yaml.dump(mapOf("dummy" to data), writer)
        return YamlConfiguration().apply { loadFromString(writer.toString()) }.get("dummy") as ConfigurationSerializable
    }

}