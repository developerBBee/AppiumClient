package repository

import data.EMU_SAMPLE_TARGET
import data.Target
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import util.Constant
import util.DefaultJson
import util.IOScope
import java.io.File

object TargetsRepository {
    private val targetsFile: File
    private val _targetsFlow = MutableStateFlow<List<Target>>(emptyList())
    private val targetsFlow: Flow<List<Target>> = _targetsFlow.filterNot { it.isEmpty() }

    init {
        val userHome = System.getProperty(Constant.USER_HOME)
        val appDir = File(userHome, Constant.APP_DIR)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        targetsFile = File(appDir, Constant.TARGETS_FILE_NAME)

        IOScope.launch {
            _targetsFlow.emit(loadTargets())
        }
    }

    private fun loadTargets(): List<Target> {
        return runCatching {
            val json = targetsFile.readText()
            DefaultJson.decodeFromString<List<Target>>(json)
        }.getOrElse {
            listOf(EMU_SAMPLE_TARGET)
        }
    }

    suspend fun saveConfig(targets: List<Target>) {
        val json = DefaultJson.encodeToString(targets)
        targetsFile.writeText(json)
        _targetsFlow.emit(targets)
    }

    fun getTargetsFlow(): Flow<List<Target>> {
        return targetsFlow
    }
}
