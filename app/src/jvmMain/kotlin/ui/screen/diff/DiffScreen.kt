package ui.screen.diff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.screen.common.component.CheckboxWithLabel
import ui.screen.common.component.DropdownWithLabel
import ui.screen.common.extension.composeViewModel
import util.decodeToBitmapPainter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

@Composable
fun DiffScreen(
    navController: NavHostController,
    viewModel: DiffViewModel = composeViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    DiffContent(
        uiState = uiState,
        onViewerSettingChange = viewModel::changeViewerSetting,
        onDirSelected = viewModel::changeDir,
        onFileSelected = viewModel::changeFile,
        onBack = navController::popBackStack,
    )
}

@Composable
private fun DiffContent(
    uiState: DiffUiState,
    onViewerSettingChange: (ViewerSetting) -> Unit,
    onDirSelected: (SelectedDirInfo) -> Unit,
    onFileSelected: (ComparedFile) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(fraction = 0.9f)
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable(enabled = false) {},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                DiffUiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth().offset(y = (-8).dp))

                DiffUiState.Empty -> Text(text = "スクリーンショットがありません。")

                is DiffUiState.Success -> {
                    DiffMainContent(
                        modifier = Modifier.fillMaxSize(),
                        dirs = state.dirs,
                        viewerSetting = state.viewerSetting,
                        onViewerSettingChange = onViewerSettingChange,
                        selectedDirInfo = state.selectedDirInfo,
                        onDirSelected = onDirSelected,
                        selectedFileInfo = state.selectedFileInfo,
                        onFileSelected = onFileSelected,
                    )
                    if (state.progress) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().offset(y = (-8).dp))
                    }
                }

                is DiffUiState.Error -> Text(text = state.throwable.stackTraceToString())
            }
        }

        OutlinedButton(onClick = onBack) {
            Text(text = "閉じる")
        }
    }
}

@Composable
private fun DiffMainContent(
    modifier: Modifier = Modifier,
    dirs: List<Path>,
    viewerSetting: ViewerSetting,
    onViewerSettingChange: (ViewerSetting) -> Unit,
    selectedDirInfo: SelectedDirInfo,
    onDirSelected: (SelectedDirInfo) -> Unit,
    selectedFileInfo: SelectedFileInfo?,
    onFileSelected: (ComparedFile) -> Unit,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 比較対象１
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ１",
                currentItemText = selectedDirInfo.leftDir.name,
                itemNames = dirs.map { it.name },
                onSelectedIndex = { onDirSelected(selectedDirInfo.copy(leftDir = dirs[it])) }
            )

            // 比較対象２
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ２",
                currentItemText = selectedDirInfo.rightDir.name,
                itemNames = dirs.map { it.name },
                onSelectedIndex = { onDirSelected(selectedDirInfo.copy(rightDir = dirs[it])) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // 画像差分表示を切替チェックボックス
            CheckboxWithLabel(
                modifier = Modifier.width(140.dp),
                textStyle = MaterialTheme.typography.body2.copy(textAlign = TextAlign.End),
                label = "画像差分表示",
                checked = viewerSetting.useImageDiff,
                onCheckedChange = { onViewerSettingChange(viewerSetting.copy(useImageDiff = it)) }
            )

            // 画像差分有のみにフィルターするチェックボックス
            CheckboxWithLabel(
                modifier = Modifier.width(140.dp),
                textStyle = MaterialTheme.typography.body2.copy(textAlign = TextAlign.End),
                label = "差分有のみ",
                checked = viewerSetting.diffOnly,
                onCheckedChange = { onViewerSettingChange(viewerSetting.copy(diffOnly = it)) }
            )

            // 画像差分有のみにフィルターするチェックボックス
            CheckboxWithLabel(
                modifier = Modifier.width(140.dp),
                textStyle = MaterialTheme.typography.body2.copy(textAlign = TextAlign.End),
                label = "NO_NAME除外",
                checked = viewerSetting.noNameExclude,
                onCheckedChange = { onViewerSettingChange(viewerSetting.copy(noNameExclude = it)) }
            )
        }

        // 選択したフォルダのファイル比較一覧表示
        DiffResult(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            selectedDirInfo = selectedDirInfo,
            selectedFileInfo = selectedFileInfo,
            onFileSelected = onFileSelected,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DiffResult(
    modifier: Modifier = Modifier,
    selectedDirInfo: SelectedDirInfo,
    selectedFileInfo: SelectedFileInfo?,
    onFileSelected: (ComparedFile) -> Unit,
) {
    Row(modifier = modifier) {
        // 左画像
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center,
        ) {
            selectedFileInfo
                ?.leftImagePath
                ?.decodeToBitmapPainter()
                ?.let { bmpPainter ->
                    Image(painter = bmpPainter, contentDescription = "左の画像")
                }
        }

        // 比較結果一覧
        DiffResultContent(
            modifier = Modifier.weight(4f),
            comparedFiles = selectedDirInfo.comparedFiles,
            selectedItem = selectedFileInfo?.selectedFile,
            onSelect = onFileSelected,
        )

        // 右画像
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center,
        ) {
            selectedFileInfo
                ?.rightImagePath
                ?.decodeToBitmapPainter()
                ?.let { bmpPainter ->
                    Image(painter = bmpPainter, contentDescription = "右の画像")
                }
        }
    }
}

@Composable
private fun DiffResultContent(
    modifier: Modifier = Modifier,
    comparedFiles: List<ComparedFile>,
    selectedItem: ComparedFile?,
    onSelect: (ComparedFile) -> Unit,
) {
    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            comparedFiles.forEach {
                TextButton(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = if (it == selectedItem) Color.Gray.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    onClick = { onSelect(it) }
                ) {
                    Text(
                        text = it.fileName,
                        color = when (it.result) { // TODO 要UI改善
                            CompareResult.LEFT_ONLY -> Color.Red
                            CompareResult.RIGHT_ONLY -> Color.Blue
                            CompareResult.SAME -> Color.Black
                            CompareResult.DIFFERENCE -> Color.Magenta
                        }
                    )
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}

@Composable
@Preview
private fun DiffContentPreview() {
    val dirs = listOf(Path("~/test1"), Path("~/test2"))
    val uiState = DiffUiState.Success(
        dirs = dirs,
        viewerSetting = ViewerSetting(),
        selectedDirInfo = SelectedDirInfo(leftDir = dirs[0], rightDir = dirs[1], comparedFiles = emptyList()),
        selectedFileInfo = null,
    )

    DiffContent(
        uiState = uiState,
        onViewerSettingChange = {},
        onDirSelected = {},
        onFileSelected = {},
        onBack = {}
    )
}
