package repository

import data.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import util.SCREENSHOT_DIR_PATH
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object ScreenshotRepository {

    private var currentTarget: Target? = null
    private val _screenshotDirsFlow = MutableStateFlow<List<Path>>(emptyList())
    val screenshotDirsFlow: StateFlow<List<Path>> = _screenshotDirsFlow.asStateFlow()

    fun changeTarget(target: Target) {
        currentTarget = target
        refreshCurrentTarget()
    }

    suspend fun refreshCurrentTarget() {
        val target = currentTarget ?: return

        val screenshotDirs = withContext(Dispatchers.IO) {
            val targetDir = SCREENSHOT_DIR_PATH / target.name

            if (targetDir.isDirectory()) {
                targetDir.listDirectoryEntries()
                    .filter { it.isDirectory() }
                    .sortedByDescending { it.name }
            } else {
                emptyList()
            }
        }

        _screenshotDirsFlow.value = screenshotDirs
    }
}
