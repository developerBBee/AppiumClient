package screen.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.component.indicator.KodeeProgressIndicator
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    onConfigClick: () -> Unit,
    mainViewModel: MainViewModel = viewModel(),
) {
    val mainStates by mainViewModel.mainStateFlow.collectAsState()

    val dataList = mainStates.map { (targetId, mainState) ->
        when (val state = mainState.runState) {
            MainRunState.Idle -> {
                MainScreenData(
                    targetName = mainState.targetName,
                    onButtonClick = { mainViewModel.run(targetId) }
                )
            }

            is MainRunState.Running -> {
                MainScreenData(
                    targetName = mainState.targetName,
                    configEnabled = false,
                    progress = true,
                    message = state.log,
                    buttonText = "キャンセル",
                    onButtonClick = { mainViewModel.cancel(targetId) },
                )
            }

            is MainRunState.Cancelling -> {
                MainScreenData(
                    targetName = mainState.targetName,
                    configEnabled = false,
                    progress = true,
                    message = state.log,
                    buttonText = "キャンセル中",
                )
            }

            is MainRunState.Finished -> {
                MainScreenData(
                    targetName = mainState.targetName,
                    message = "終了 [実行完了=${state.isCompletion}]",
                    onButtonClick = { mainViewModel.run(targetId) },
                )
            }

            is MainRunState.Error -> {
                MainScreenData(
                    targetName = mainState.targetName,
                    message = state.message,
                    showCopyToClipboard = true,
                    onButtonClick = { mainViewModel.run(targetId) },
                )
            }
        }
    }

    MainBaseLayout(
        modifier = modifier,
        scope = scope,
        dataList = dataList,
        onConfigClick = onConfigClick,
    )
}

@Composable
private fun MainBaseLayout(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    dataList: List<MainScreenData>,
    onConfigClick: () -> Unit,
) {
    Column(modifier = modifier) {
        IconButton(
            enabled = dataList.map { it.configEnabled }.all { it },
            onClick = onConfigClick
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                tint = MaterialTheme.colors.primaryVariant,
                contentDescription = "設定"
            )
        }

        if (dataList.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val pagerState = rememberPagerState { dataList.size }

            TabRow(
                modifier = Modifier.background(color = Color.White),
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primaryVariant,
            ) {
                dataList.forEachIndexed { index, data ->
                    Tab(
                        text = { Text(text = data.targetName, style = MaterialTheme.typography.button) },
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
            ) { page ->
                MainPagerContent(
                    screenData = dataList[page],
                    scope = scope,
                )
            }
        }
    }
}

@Composable
private fun MainPagerContent(
    modifier: Modifier = Modifier,
    screenData: MainScreenData,
    scope: CoroutineScope,
) {
    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Box(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (screenData.progress) {
                        KodeeProgressIndicator()
                    }
                }

                OutlinedButton(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .width(240.dp)
                        .height(80.dp),
                    elevation = ButtonDefaults.elevation(),
                    onClick = {
                        scope.launch {
                            screenData.onButtonClick()
                        }
                    }
                ) {
                    Text(
                        text = screenData.buttonText,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.h4,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier.fillMaxWidth(0.8f),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = screenData.message,
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        if (screenData.showCopyToClipboard) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    val selection = StringSelection(screenData.message)
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    tint = MaterialTheme.colors.primaryVariant,
                    contentDescription = "コピー"
                )
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(scrollState),
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainPagerContent(
        modifier = Modifier.fillMaxSize(),
        screenData = MainScreenData(targetName = "Preview"),
        scope = rememberCoroutineScope(),
    )
}

data class MainScreenData(
    val targetName: String,
    val configEnabled: Boolean = true,
    val progress: Boolean = false,
    val message: String = "",
    val showCopyToClipboard: Boolean = false,
    val buttonText: String = "実行",
    val onButtonClick: suspend () -> Unit = {},
)
