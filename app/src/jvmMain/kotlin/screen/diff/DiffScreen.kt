package screen.diff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import screen.common.component.CheckboxWithLabel
import screen.common.component.DropdownWithLabel
import util.decodeToBitmapPainter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

@Composable
fun DiffScreen(
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
    viewModel: DiffViewModel = viewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadDirs() }

    DiffDialog(
        onOutsideClick = onOutsideClick,
        onCloseClick = onCloseClick,
        uiState = uiState,
        onUseImageDiffChange = viewModel::changeUseImageDiff,
        onDirSelected = viewModel::changeDir,
        onFileSelected = viewModel::changeFile,
    )
}

@Composable
private fun DiffDialog(
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
    uiState: DiffUiState,
    onUseImageDiffChange: (Boolean) -> Unit,
    onDirSelected: (SelectedDirInfo) -> Unit,
    onFileSelected: (ComparedFile) -> Unit,
) {
    Dialog(
        onDismissRequest = onOutsideClick,
        properties = DialogProperties(usePlatformDefaultWidth = false)
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
                    DiffUiState.Loading -> CircularProgressIndicator()

                    DiffUiState.Empty -> Text(text = "スクリーンショットがありません。")

                    is DiffUiState.Success ->
                        DiffContent(
                            modifier = Modifier.fillMaxSize(),
                            dirs = state.dirs,
                            useImageDiff = state.useImageDiff,
                            onUseImageDiffChange = onUseImageDiffChange,
                            selectedDirInfo = state.selectedDirInfo,
                            onDirSelected = onDirSelected,
                            selectedFileInfo = state.selectedFileInfo,
                            onFileSelected = onFileSelected,
                        )

                    is DiffUiState.Error -> Text(text = state.throwable.stackTraceToString())
                }
            }

            OutlinedButton(onClick = onCloseClick) {
                Text(text = "閉じる")
            }
        }
    }
}

@Composable
private fun DiffContent(
    modifier: Modifier = Modifier,
    dirs: List<Path>,
    useImageDiff: Boolean,
    onUseImageDiffChange: (Boolean) -> Unit,
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

        // 画像差分表示を切替チェックボックス
        CheckboxWithLabel(
            modifier = Modifier.width(120.dp).align(Alignment.CenterHorizontally),
            label = "画像差分表示",
            checked = useImageDiff,
            onCheckedChange = onUseImageDiffChange
        )

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
        useImageDiff = false,
        selectedDirInfo = SelectedDirInfo(leftDir = dirs[0], rightDir = dirs[1], comparedFiles = emptyList()),
        selectedFileInfo = null,
    )

    DiffDialog(
        onOutsideClick = {},
        onCloseClick = {},
        uiState = uiState,
        onUseImageDiffChange = {},
        onDirSelected = {},
        onFileSelected = {},
    )
}
