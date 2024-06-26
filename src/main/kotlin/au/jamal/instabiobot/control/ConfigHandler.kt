package au.jamal.instabiobot.control

import au.jamal.instabiobot.utilities.Log
import org.yaml.snakeyaml.Yaml
import java.io.File
import kotlin.system.exitProcess

data class ConfigSettings(
    val productionMode: Boolean = true,
    val debugMode: Boolean = false,
    val timeoutSeconds: Long = 15,
    val failLimit: Int = 3,
    val restartInDays: Long = 9,
)

object ConfigHandler {

    private const val CONFIG_PATH = "config.yml"
    private val configFile = File(CONFIG_PATH)
    private val configClass = ConfigSettings::class.java
    private val yaml = Yaml()

    fun loadSettings(): ConfigSettings {
        if (!configFile.exists()) {
            Log.alert("No config file exists, exiting...")
            exitProcess(0)
        }
        val settings = getConfig()
        Log.status("Loaded config:")
        Log.dump(settings)
        return settings
    }

    private fun getConfig(): ConfigSettings {
        val inputStream = configFile.inputStream()
        inputStream.use {
            val data = yaml.load(inputStream) as Map<String, Any>
            val args = configClass.declaredFields.map { field ->
                val fieldName = field.name
                val fieldValue = data[fieldName] ?: field.get(ConfigSettings())
                fieldValue
            }.toTypedArray()
            return constructSettings(args, data)
        }
    }

    private fun constructSettings(args: Array<Any>, data: Map<String, Any>): ConfigSettings {
        try{
            val constructor = configClass.constructors.find { it.parameterCount == data.size }
                ?: throw IllegalArgumentException("No matching constructor found")
            return constructor.newInstance(*args) as ConfigSettings
        } catch (e: IllegalArgumentException) {
            Log.alert("Failed to load config, verify expected types:")
            val expectedTypes = getExpectedTypes()
            Log.dump(expectedTypes)
            Log.alert("Exiting session launch...")
            exitProcess(0)
        }
    }

    private fun getExpectedTypes(): String {
        val configFields = ConfigSettings::class.java.declaredFields
        val keyTypeArray = configFields.map { field ->
            field.name to field.type.simpleName
        }.toTypedArray()
        return keyTypeArray.contentToString()
    }

}