package ui.screen.diff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import usecase.CompareFilesUseCase
import usecase.GetImagePathUseCase
import usecase.GetScreenshotDirsUseCase
import java.nio.file.Path
import kotlin.io.path.div

class DiffViewModel : ViewModel() {
    private val _uiStateFlow = MutableStateFlow<DiffUiState>(DiffUiState.Loading)
    val uiStateFlow: StateFlow<DiffUiState> = _uiStateFlow.asStateFlow()

    private val _dirsFlow = MutableStateFlow<List<Path>>(emptyList())
    private val notEmptyDirsFlow = _dirsFlow.filter { it.isNotEmpty() }

    private val useImageDiffFlow = MutableStateFlow(false)

    private val _selectedDirFlow = MutableStateFlow<SelectedDirInfo?>(null)
    private val selectedDirFlow: Flow<SelectedDirInfo> = _selectedDirFlow.filterNotNull()

    private val _selectedFileFlow = MutableStateFlow<SelectedFileInfo?>(null)

    init {
        // UI更新用パラメータ監視
        combine(
            notEmptyDirsFlow,
            useImageDiffFlow,
            selectedDirFlow,
            _selectedFileFlow // File選択はNullableのFlowを使用する
        ) { dirs, useImageDiff, dirInfo, fileInfo ->
            _uiStateFlow.value = DiffUiState.Success(
                dirs = dirs,
                useImageDiff = useImageDiff,
                selectedDirInfo = dirInfo,
                selectedFileInfo = fileInfo,
            )
        }
        .launchIn(viewModelScope)

        // フォルダが選ばれると２つのフォルダ内の同名ファイルを比較して、SelectedDirInfoを更新する
        selectedDirFlow
            .onEach {
                val comparedFiles = compareFiles(leftDir = it.leftDir, rightDir = it.rightDir)
                val selectedDirInfo = SelectedDirInfo(
                    leftDir = it.leftDir,
                    rightDir = it.rightDir,
                    comparedFiles = comparedFiles,
                )
                _selectedDirFlow.value = selectedDirInfo
            }
            .launchIn(viewModelScope)

        // ファイル選択か差分使用設定変更すると、２つのフォルダ内の同名ファイルを比較して、SelectedFileInfoを更新する
        combine(
            selectedDirFlow,
            _selectedFileFlow,
            useImageDiffFlow,
        ) { dirInfo, fileInfo, useImageDiff ->
            if (fileInfo == null) return@combine

            val left = dirInfo.leftDir / fileInfo.selectedFile.fileName
            val right = dirInfo.rightDir / fileInfo.selectedFile.fileName

            val images = compareImage(left = left, right = right, useImageDiff = useImageDiff)

            _selectedFileFlow.value = fileInfo.copy(leftImagePath = images.first, rightImagePath = images.second)
        }
        .launchIn(viewModelScope)

        // フォルダを取得する（１回のみ）
        GetScreenshotDirsUseCase()
            .take(1)
            .onEach { dirs ->
                if (dirs.isEmpty()) {
                    _uiStateFlow.value = DiffUiState.Empty
                } else {
                    _dirsFlow.value = dirs
                }
            }
            .catch { _uiStateFlow.value = DiffUiState.Error(throwable = it) }
            .launchIn(viewModelScope)

        // フォルダが取得できた場合の初期選択
        notEmptyDirsFlow
            .onEach {
                val dirInfo = SelectedDirInfo(
                    leftDir = it[0],
                    rightDir = it[0],
                    comparedFiles = emptyList(),
                )
                changeDir(dirInfo = dirInfo)
            }
            .take(1)
            .launchIn(viewModelScope)
    }

    private fun compareFiles(leftDir: Path, rightDir: Path): List<ComparedFile> {
        return CompareFilesUseCase(leftDir = leftDir, rightDir = rightDir)
    }

    private suspend fun compareImage(
        left: Path,
        right: Path,
        useImageDiff: Boolean,
    ): Pair<Path?, Path?> = withContext(Dispatchers.IO) {
        GetImagePathUseCase(
            leftFilePath = left,
            rightFilePath = right,
            useImageDiff = useImageDiff
        )
    }

    fun changeUseImageDiff(use: Boolean) {
        useImageDiffFlow.value = use
    }

    fun changeDir(dirInfo: SelectedDirInfo) {
        _selectedFileFlow.value = null
        _selectedDirFlow.value = dirInfo
    }

    fun changeFile(file: ComparedFile) {
        _selectedFileFlow.value = SelectedFileInfo(selectedFile = file)
    }
}
