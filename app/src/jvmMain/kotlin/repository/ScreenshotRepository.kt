package repository

import data.Target
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import util.SCREENSHOT_DIR_PATH
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object ScreenshotRepository {

    private val _screenshotDirsFlow = MutableStateFlow<List<Path>>(emptyList())
    val screenshotDirsFlow: StateFlow<List<Path>> = _screenshotDirsFlow.asStateFlow()

    fun changeTarget(target: Target) {

        val targetDir = SCREENSHOT_DIR_PATH / target.name

        val screenshotDirs = if (targetDir.isDirectory()) {
            targetDir.listDirectoryEntries()
                .filter { it.isDirectory() }
                .sortedByDescending { it.name }
        } else {
            emptyList()
        }

        _screenshotDirsFlow.value = screenshotDirs
    }
}
