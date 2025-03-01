package screen.diff

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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import util.createImageDifference
import util.decodeToBitmapPainter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.name

@Composable
fun DiffScreen(
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
    viewModel: DiffViewModel = viewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    DiffDialog(
        onOutsideClick = onOutsideClick,
        onCloseClick = onCloseClick,
        uiState = uiState,
        onCompareClick = viewModel::compareFiles
    )
}

@Composable
private fun DiffDialog(
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
    uiState: DiffUiState,
    onCompareClick: (Pair<Path, Path>) -> Unit
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

                    is DiffUiState.Loaded,
                    is DiffUiState.Compared -> {
                        if (state.dirs.isEmpty()) {
                            Text(text = "スクリーンショットがありません。")
                        } else {
                            DiffContent(
                                modifier = Modifier.fillMaxSize(),
                                dirs = state.dirs,
                                results = if (state is DiffUiState.Compared) {
                                    state.results
                                } else {
                                    emptyList()
                                },
                                onCompareClick = onCompareClick
                            )
                        }
                    }

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
    results: List<CompareFileResult>,
    onCompareClick: (Pair<Path, Path>) -> Unit,
) {
    var leftIndex by remember { mutableIntStateOf(0) }
    var rightIndex by remember { mutableIntStateOf(0) }
    var showDiffImage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 比較対象１
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ１",
                currentItemText = dirs[leftIndex].name,
                itemNames = dirs.map { it.name },
                onSelectedIndex = { leftIndex = it }
            )

            // 比較対象２
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ２",
                currentItemText = dirs[rightIndex].name,
                itemNames = dirs.map { it.name },
                onSelectedIndex = { rightIndex = it }
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(120.dp))

            OutlinedButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { onCompareClick(dirs[leftIndex] to dirs[rightIndex]) }
            ) {
                Text(text = "比較する")
            }

            CheckboxWithLabel(
                modifier = Modifier.width(120.dp),
                label = "差分画像表示",
                checked = showDiffImage,
                onCheckedChange = { showDiffImage = it }
            )
        }

        DiffResult(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            leftDirPath = dirs[leftIndex],
            rightDirPath = dirs[rightIndex],
            showDiffImage = showDiffImage,
            results = results,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DiffResult(
    modifier: Modifier = Modifier,
    leftDirPath: Path,
    rightDirPath: Path,
    showDiffImage: Boolean,
    results: List<CompareFileResult>,
) {
    var leftFilePath: Path? by remember { mutableStateOf(null) }
    var rightFilePath: Path? by remember { mutableStateOf(null) }

    Row(modifier = modifier) {
        // 左画像
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center,
        ) {
            leftFilePath
                ?.decodeToBitmapPainter()
                ?.let { Image(it, "左の画像") }
        }

        // 比較結果一覧
        DiffResultContent(
            modifier = Modifier.weight(4f),
            results = results,
            onSelect = { fileName ->
                leftFilePath = leftDirPath / fileName
                rightFilePath = rightDirPath / fileName
            }
        )

        // 右画像
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center,
        ) {
            val left = leftFilePath
            val right = rightFilePath
            if (left == null || right == null) return

            val rightImagePath = if (showDiffImage) {
                createImageDifference(left, right)
            } else {
                rightFilePath
            }

            rightImagePath
                ?.decodeToBitmapPainter()
                ?.let { Image(it, "右の画像") }
        }
    }
}

@Composable
private fun DiffResultContent(
    modifier: Modifier = Modifier,
    results: List<CompareFileResult>,
    onSelect: (String) -> Unit,
) {
    var selectedItem: CompareFileResult? by remember { mutableStateOf(null) }

    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            results.forEach {
                TextButton(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = if (it == selectedItem) Color.Gray.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    onClick = {
                        selectedItem = it
                        onSelect(it.fileName)
                    }
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
    val uiState = DiffUiState.Loaded(dirs = listOf(Path("~/test1"), Path("~/test2")))

    DiffDialog(
        onOutsideClick = {},
        onCloseClick = {},
        uiState = uiState,
        onCompareClick = {}
    )
}
