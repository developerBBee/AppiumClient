package screen.diff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import screen.common.component.DropdownWithLabel
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 比較対象１
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ１",
                currentItemText = dirs[0].name, // TODO 選択したDirにする
                itemNames = dirs.map { it.name },
                onSelectedIndex = { /* TODO */ }
            )

            // 比較対象２
            DropdownWithLabel(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                label = "比較フォルダ２",
                currentItemText = dirs[1].name, // TODO 選択したDirにする
                itemNames = dirs.map { it.name },
                onSelectedIndex = { /* TODO */ }
            )
        }

        OutlinedButton(onClick = { onCompareClick(dirs[0] to dirs[1]) }) { // TODO 選択したDirにする
            Text(text = "比較する")
        }

        DiffResult(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp),
            results = results,
        )
    }
}

@Composable
private fun DiffResult(
    modifier: Modifier = Modifier,
    results: List<CompareFileResult>,
) {
    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "比較結果"
            )

            results.forEach {
                Text(text = it.fileName, color = when (it.result) { // TODO 要UI改善
                    CompareResult.LEFT_ONLY -> Color.Red
                    CompareResult.RIGHT_ONLY -> Color.Blue
                    CompareResult.SAME -> Color.Black
                    CompareResult.DIFFERENCE -> Color.Magenta
                })
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
