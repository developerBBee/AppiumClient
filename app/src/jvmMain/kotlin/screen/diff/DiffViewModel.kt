package screen.diff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import usecase.CompareFilesUseCase
import usecase.GetScreenshotDirsUseCase
import java.nio.file.Path

class DiffViewModel : ViewModel() {
    private val _uiStateFlow = MutableStateFlow<DiffUiState>(DiffUiState.Loading)
    val uiStateFlow: StateFlow<DiffUiState> = _uiStateFlow.asStateFlow()

    init {
        GetScreenshotDirsUseCase()
            .onEach { _uiStateFlow.value = DiffUiState.Loaded(it) }
            .catch { _uiStateFlow.value = DiffUiState.Error(it) }
            .launchIn(viewModelScope)
    }

    fun compareFiles(dirPair: Pair<Path, Path>) {
        val results = CompareFilesUseCase(leftDir = dirPair.first, rightDir = dirPair.second)
        _uiStateFlow.update { DiffUiState.Compared(dirs = it.dirs, results = results) }
    }
}

sealed class DiffUiState(val dirs: List<Path> = emptyList()) {
    data object Loading : DiffUiState()
    class Loaded(dirs: List<Path>) : DiffUiState(dirs = dirs)
    class Compared(dirs: List<Path>, val results: List<CompareFileResult>) : DiffUiState(dirs = dirs)
    data class Error(val throwable: Throwable) : DiffUiState()
}

data class CompareFileResult(
    val fileName: String,
    val result: CompareResult,
)

enum class CompareResult {
    LEFT_ONLY,
    RIGHT_ONLY,
    DIFFERENCE,
    SAME,
}
