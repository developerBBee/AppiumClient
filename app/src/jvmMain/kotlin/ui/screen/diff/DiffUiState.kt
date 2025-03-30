package ui.screen.diff

import java.nio.file.Path

sealed interface DiffUiState {
    data object Loading : DiffUiState
    data object Empty : DiffUiState
    data class Error(val throwable: Throwable) : DiffUiState

    data class Success(
        val progress: Boolean = false,
        val dirs: List<Path>,
        val viewerSetting: ViewerSetting,
        val selectedDirInfo: SelectedDirInfo,
        val selectedFileInfo: SelectedFileInfo?,
    ) : DiffUiState
}

data class SelectedDirInfo(
    val leftDir: Path,
    val rightDir: Path,
    val comparedFiles: List<ComparedFile>,
)

data class SelectedFileInfo(
    val selectedFile: ComparedFile,
    val leftImagePath: Path? = null,
    val rightImagePath: Path? = null,
)

data class ComparedFile(
    val fileName: String,
    val result: CompareResult,
)

enum class CompareResult {
    LEFT_ONLY,
    RIGHT_ONLY,
    DIFFERENCE,
    SAME,
}
