package ui.screen.main

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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import data.TargetId
import data.senario.SAMPLE_PHONE_EMU_SCENARIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.navigation.ScreenRoute
import ui.screen.common.extension.composeViewModel
import ui.screen.main.component.VerticalDivider
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    scope: CoroutineScope,
    mainViewModel: MainViewModel = composeViewModel(),
) {
    val mainStates by mainViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val errorStates by mainViewModel.errorFlow.collectAsStateWithLifecycle()

    val error = errorStates
    if (error != null) {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            Text(text = error.stackTraceToString())
        }
    } else {
        MainBaseLayout(
            modifier = modifier,
            scope = scope,
            stateList = mainStates,
            onTargetChanged = mainViewModel::changeCurrentTarget,
            onConfigClick = { navController.navigate(ScreenRoute.Config) },
            onDiffClick = { navController.navigate(ScreenRoute.Diff) },
            onButtonClick = { state ->
                when (state.buttonState) {
                    ButtonState.RUNNABLE -> mainViewModel.run(state.targetId)
                    ButtonState.CANCELABLE -> mainViewModel.cancel(state.targetId)
                    ButtonState.DISABLE -> {}
                }
            }
        )
    }
}

@Composable
private fun MainBaseLayout(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    stateList: List<MainUiState>,
    onTargetChanged: (TargetId) -> Unit,
    onConfigClick: () -> Unit,
    onDiffClick: () -> Unit,
    onButtonClick: (MainUiState) -> Unit,
) {
    val iconButtonsEnabled = stateList.all { !it.progress }

    Column(modifier = modifier) {
        Row {
            IconButton(
                enabled = iconButtonsEnabled,
                onClick = onConfigClick
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    tint = MaterialTheme.colors.primaryVariant,
                    contentDescription = "設定"
                )
            }

            IconButton(
                enabled = iconButtonsEnabled,
                onClick = onDiffClick
            ) {
                Icon(
                    imageVector = Icons.Default.Difference,
                    tint = MaterialTheme.colors.primaryVariant,
                    contentDescription = "スクリーンショット差分"
                )
            }
        }

        if (stateList.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val pagerState = rememberPagerState { stateList.size }
            val currentPage = pagerState.currentPage

            LaunchedEffect(currentPage) {
                onTargetChanged(stateList[currentPage].targetId)
            }

            TabRow(
                modifier = Modifier.background(color = Color.White),
                selectedTabIndex = currentPage,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primaryVariant,
            ) {
                stateList.forEachIndexed { index, data ->
                    Tab(
                        text = { Text(text = data.targetName, style = MaterialTheme.typography.button) },
                        selected = currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                    )
                }
            }

            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
            ) { page ->
                MainPagerContent(
                    state = stateList[page],
                    onButtonClick = onButtonClick,
                )
            }
        }
    }
}

@Composable
private fun MainPagerContent(
    modifier: Modifier = Modifier,
    state: MainUiState,
    onButtonClick: (MainUiState) -> Unit,
) {
    Box(modifier = modifier) {
        val scrollState = rememberScrollState()

        SelectionContainer(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Box(
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if (state.progress) {
                            CircularProgressIndicator()
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .width(240.dp)
                            .height(80.dp),
                        elevation = ButtonDefaults.elevation(),
                        onClick = { onButtonClick(state) }
                    ) {
                        Text(
                            text = state.buttonText,
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
                        text = state.message,
                        style = MaterialTheme.typography.body1,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(0.8f),
                ) {
                    Divider()

                    // Header
                    ActionRow(
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        actionIndex = "番号",
                        actionName = "アクション名",
                        actionTarget = "対象",
                        style = MaterialTheme.typography.h6
                            .copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                    )

                    Divider()

                    // Contents
                    state.actions.forEachIndexed { index, action ->
                        ActionRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    color = if (index == state.currentIndex) {
                                        MaterialTheme.colors.secondary.copy(alpha = 0.3f)
                                    } else {
                                        MaterialTheme.colors.background
                                    }
                                ),
                            actionIndex = index.toString(),
                            actionName = action.getActionName(),
                            actionTarget = action.getActionTarget(),
                            style = MaterialTheme.typography.body1
                                .copy(textAlign = TextAlign.Center),
                        )

                        Divider()
                    }
                }
            }
        }

        if (state.showCopyToClipboard) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    val selection = StringSelection(state.message)
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

@Composable
private fun ActionRow(
    modifier: Modifier = Modifier,
    actionIndex: String,
    actionName: String,
    actionTarget: String,
    style: TextStyle,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VerticalDivider()

        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
            text = actionIndex,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        VerticalDivider()

        Text(
            modifier = Modifier.weight(2f).padding(horizontal = 2.dp),
            text = actionName,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        VerticalDivider()

        Text(
            modifier = Modifier.weight(2f).padding(horizontal = 2.dp),
            text = actionTarget,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        VerticalDivider()
    }
}


@Preview
@Composable
private fun MainScreenPreview() {
    MainBaseLayout(
        modifier = Modifier.fillMaxSize(),
        scope = rememberCoroutineScope(),
        stateList = listOf(
            MainUiState(
                targetId = TargetId(0),
                targetName = "Preview1",
                currentIndex = 3,
                actions = SAMPLE_PHONE_EMU_SCENARIO.getActions(),
                runState = MainRunState.Running
            ),
            MainUiState(
                targetId = TargetId(1),
                targetName = "Preview2",
                currentIndex = 0,
                actions = SAMPLE_PHONE_EMU_SCENARIO.getActions(),
                runState = MainRunState.Idle
            ),
        ),
        onTargetChanged = {},
        onConfigClick = {},
        onDiffClick = {},
        onButtonClick = {}
    )
}
