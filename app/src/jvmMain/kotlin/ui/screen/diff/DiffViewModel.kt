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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import usecase.CompareFilesUseCase
import usecase.GetImagePathUseCase
import usecase.GetScreenshotDirsUseCase
import usecase.RefreshScreenshotDirsUseCase
import java.nio.file.Path
import kotlin.io.path.div

class DiffViewModel : ViewModel() {
    private val _uiStateFlow = MutableStateFlow<DiffUiState>(DiffUiState.Loading)
    val uiStateFlow: StateFlow<DiffUiState> = _uiStateFlow.asStateFlow()

    private val _dirsFlow = MutableStateFlow<List<Path>>(emptyList())
    private val notEmptyDirsFlow = _dirsFlow.filter { it.isNotEmpty() }

    private val viewerSettingFlow = MutableStateFlow(ViewerSetting())

    private val _selectedDirFlow = MutableStateFlow<SelectedDirInfo?>(null)
    private val selectedDirFlow: Flow<SelectedDirInfo> = _selectedDirFlow.filterNotNull()

    private val _selectedFileFlow = MutableStateFlow<SelectedFileInfo?>(null)

    private val selectDirProgressFlow = MutableStateFlow(false)
    private val selectFileProgressFlow = MutableStateFlow(false)

    private val progressFlow: Flow<Boolean> = combine(
        selectDirProgressFlow,
        selectFileProgressFlow,
    ) { dir, file ->
        dir || file
    }.distinctUntilChanged()

    init {
        // UI更新用パラメータ監視
        combine(
            notEmptyDirsFlow,
            viewerSettingFlow,
            selectedDirFlow,
            _selectedFileFlow, // File選択は未選択でもUIに表示するためNullableのFlowを使用する
            progressFlow,
        ) { dirs, viewerSetting, dirInfo, fileInfo, progress ->
            _uiStateFlow.value = DiffUiState.Success(
                progress = progress,
                dirs = dirs,
                viewerSetting = viewerSetting,
                selectedDirInfo = dirInfo,
                selectedFileInfo = fileInfo,
            )
        }.launchIn(viewModelScope)

        // フォルダが選ばれると２つのフォルダ内の同名ファイルを比較して、SelectedDirInfoを更新する
        // ViewerSettingの変更があった場合に、comparedFilesのフィルタリングを更新する
        combine(
            selectedDirFlow,
            viewerSettingFlow,
        ) { dirInfo, setting ->
            selectDirProgressFlow.value = true
            val comparedFiles = CompareFilesUseCase(
                leftDir = dirInfo.leftDir,
                rightDir = dirInfo.rightDir,
                diffOnly = setting.diffOnly,
                noNameExclude = setting.noNameExclude,
            )

            _selectedDirFlow.value = SelectedDirInfo(
                leftDir = dirInfo.leftDir,
                rightDir = dirInfo.rightDir,
                comparedFiles = comparedFiles,
            )
            selectDirProgressFlow.value = false
        }.launchIn(viewModelScope + Dispatchers.IO)

        // ファイル選択か差分使用設定変更すると、２つのフォルダ内の同名ファイルを比較して、SelectedFileInfoを更新する
        combine(
            selectedDirFlow,
            _selectedFileFlow,
            viewerSettingFlow,
        ) { dirInfo, fileInfo, setting ->
            if (fileInfo == null) return@combine
            selectFileProgressFlow.value = true

            val left = dirInfo.leftDir / fileInfo.selectedFile.fileName
            val right = dirInfo.rightDir / fileInfo.selectedFile.fileName

            val images = compareImage(left = left, right = right, useImageDiff = setting.useImageDiff)

            _selectedFileFlow.value = fileInfo.copy(leftImagePath = images.first, rightImagePath = images.second)
            selectFileProgressFlow.value = false
        }.launchIn(viewModelScope + Dispatchers.IO)

        // フォルダを取得する
        GetScreenshotDirsUseCase()
            .onEach { dirs ->
                if (dirs.isEmpty()) {
                    _uiStateFlow.value = DiffUiState.Empty
                } else {
                    _dirsFlow.value = dirs
                }
            }
            .catch { _uiStateFlow.value = DiffUiState.Error(throwable = it) }
            .launchIn(viewModelScope + Dispatchers.IO)

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

    fun changeViewerSetting(viewerSetting: ViewerSetting) = runIfNotInProgress {
        viewerSettingFlow.value = viewerSetting
    }

    fun changeDir(dirInfo: SelectedDirInfo) = runIfNotInProgress {
        _selectedFileFlow.value = null
        _selectedDirFlow.value = dirInfo
    }

    fun changeFile(file: ComparedFile) = runIfNotInProgress {
        _selectedFileFlow.value = SelectedFileInfo(selectedFile = file)
    }

    fun refreshScreenshotDirs() = runIfNotInProgress {
        RefreshScreenshotDirsUseCase()
    }

    private fun runIfNotInProgress(block: suspend () -> Unit) {
        viewModelScope.launch {
            if (progressFlow.firstOrNull() == true) return@launch
            block()
        }
    }
}

data class ViewerSetting(
    val useImageDiff: Boolean = false,
    val diffOnly: Boolean = true,
    val noNameExclude: Boolean = true,
)
