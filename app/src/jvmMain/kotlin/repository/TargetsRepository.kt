package repository

import data.EMU_SAMPLE_TARGET
import data.Target
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import util.Constant
import util.DefaultJson
import util.IOScope
import util.USER_DIR_PATH
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object TargetsRepository {
    private val targetsFile: Path
    private val _targetsFlow = MutableStateFlow<List<Target>>(emptyList())
    private val targetsFlow: Flow<List<Target>> = _targetsFlow.filterNot { it.isEmpty() }

    init {
        val clientDirPath = USER_DIR_PATH / Constant.CLIENT_DIR
        if (!clientDirPath.exists()) {
            clientDirPath.createDirectory()
        }
        targetsFile = clientDirPath / Constant.TARGETS_FILE_NAME

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
