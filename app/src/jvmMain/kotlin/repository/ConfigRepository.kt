package repository

import data.AppiumConfiguration
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import util.Constant
import util.DefaultJson
import util.IOScope
import java.io.File

object ConfigRepository {
    private val configFile: File
    private val _configFlow = MutableStateFlow<AppiumConfiguration?>(null)
    private val configStateFlow: Flow<AppiumConfiguration> = _configFlow.filterNotNull()

    init {
        val userHome = System.getProperty(Constant.USER_HOME)
        val appDir = File(userHome, Constant.APP_DIR)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        configFile = File(appDir, Constant.CONFIG_FILE_NAME)

        IOScope.launch {
            _configFlow.emit(loadConfig())
        }
    }

    private suspend fun loadConfig(): AppiumConfiguration {
        val config = if (configFile.exists()) {
            val json = configFile.readText()
            DefaultJson.decodeFromString(json)
        } else {
            AppiumConfiguration()
        }

        saveConfig(config)

        return config
    }

    suspend fun saveConfig(config: AppiumConfiguration) {
        val json = DefaultJson.encodeToString(config)
        configFile.writeText(json)
        _configFlow.emit(config)
    }

    fun getConfigFlow(): Flow<AppiumConfiguration> {
        return configStateFlow
    }
}
