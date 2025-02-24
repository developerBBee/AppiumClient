package screen.config

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import data.AppiumConfiguration
import data.EMU_SAMPLE_TARGET
import data.PresetModel
import data.Target
import data.senario.ScenarioName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import screen.config.component.DropdownWithLabel
import screen.config.component.TextFieldWithLabel

@Composable
fun ConfigScreen(
    scope: CoroutineScope,
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
    configViewModel: ConfigViewModel = viewModel(),
) {
    val configState by configViewModel.uiState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onOutsideClick,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ConfigScreenContent(
            modifier = Modifier
                .fillMaxSize(fraction = 0.9f)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .clickable(enabled = false) {},
            scope = scope,
            configState = configState,
            onCloseClick = onCloseClick,
            onAdditionClick = configViewModel::addNewTarget,
            onRemoveClick = configViewModel::removeTarget,
            onTargetUpdate = configViewModel::updateTarget,
        )
    }
}

@Composable
private fun ConfigScreenContent(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    configState: ConfigUiState,
    onCloseClick: () -> Unit,
    onAdditionClick: () -> Unit,
    onRemoveClick: (Target) -> Unit,
    onTargetUpdate: (Int, Target) -> Unit
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = configState) {
            is ConfigUiState.Loading -> {
                CircularProgressIndicator()
            }

            is ConfigUiState.Success -> {
                TargetsTable(
                    modifier = Modifier.fillMaxSize(),
                    scope = scope,
                    targets = state.targets,
                    onAdditionClick = onAdditionClick,
                    onRemoveClick = onRemoveClick,
                    onTargetUpdate = onTargetUpdate,
                    onCloseClick = onCloseClick,
                )
            }

            is ConfigUiState.Error -> {
                Text(text = state.throwable.stackTraceToString())
            }
        }
    }
}

@Composable
private fun TargetsTable(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    targets: List<Target>,
    onAdditionClick: () -> Unit,
    onRemoveClick: (Target) -> Unit,
    onTargetUpdate: (Int, Target) -> Unit,
    onCloseClick: () -> Unit,
) {
    val pagerState = rememberPagerState { targets.size }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            OutlinedButton(onClick = onAdditionClick) {
                Text(text = "追加")
            }
            OutlinedButton(
                enabled = targets.size > 1,
                onClick = {
                    scope.launch {
                        val target = targets[pagerState.currentPage]
                        if (pagerState.currentPage >= targets.size - 1) {
                            pagerState.scrollToPage(page = pagerState.currentPage - 1)
                        }
                        onRemoveClick(target)
                    }
                }
            ) {
                Text(text = "削除")
            }
        }

        TabRow(
            modifier = Modifier.background(color = Color.White),
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primaryVariant,
        ) {
            targets.map { it.name }.forEachIndexed { index, name ->
                Tab(
                    text = { Text(text = name, style = MaterialTheme.typography.button) },
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
        ) { page ->
            val target = targets[page]
            val scrollState = rememberScrollState(0)

            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 96.dp)
                        .verticalScroll(scrollState),
                ) {
                    EditName(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        name = target.name,
                        onNameChange = { onTargetUpdate(page, target.copy(name = it)) },
                    )

                    val scenarioNames = ScenarioName.entries
                    DropdownWithLabel(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        label = "シナリオ",
                        currentItemText = target.scenarioName.value,
                        itemNames = scenarioNames.map { it.value },
                        onSelectedIndex = { onTargetUpdate(page, target.copy(scenarioName = scenarioNames[it])) },
                    )

                    val deviceInfos = PresetModel.entries.map { it.deviceInfo }
                    DropdownWithLabel(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        label = "デバイス",
                        currentItemText = target.deviceInfo.info,
                        itemNames = deviceInfos.map { it.info },
                        onSelectedIndex = { onTargetUpdate(page, target.copy(deviceInfo = deviceInfos[it])) },
                    )

                    EditConfig(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        config = target.configuration,
                        onConfigChange = { onTargetUpdate(page, target.copy(configuration = it)) },
                    )
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState),
                )
            }
        }

        OutlinedButton(onClick = onCloseClick) {
            Text(text = "閉じる")
        }
    }
}

@Composable
private fun EditName(
    modifier: Modifier = Modifier,
    name: String,
    onNameChange: (String) -> Unit,
) {
    Box(modifier = modifier) {
        TextFieldWithLabel(
            modifier = Modifier.fillMaxWidth(),
            label = "テスト名",
            value = name,
            onValueChange = onNameChange
        )
    }
}

@Composable
private fun EditConfig(
    modifier: Modifier = Modifier,
    config: AppiumConfiguration,
    onConfigChange: (AppiumConfiguration) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithLabel(
            modifier = Modifier.fillMaxWidth(),
            label = "Host",
            value = config.host,
            onValueChange = { onConfigChange(config.copy(host = it)) }
        )

        TextFieldWithLabel(
            modifier = Modifier.fillMaxWidth(),
            label = "Port",
            value = config.port.toString(),
            validator = {
                val port = it.toIntOrNull()
                (port != null && port in 0..65535)
            },
            onValueChange = { onConfigChange(config.copy(port = it.toInt())) }
        )

        TextFieldWithLabel(
            modifier = Modifier.fillMaxWidth(),
            label = "UDID",
            value = config.udid,
            onValueChange = { onConfigChange(config.copy(udid = it)) }
        )

        TextFieldWithLabel(
            modifier = Modifier.fillMaxWidth(),
            label = "AppFullPath",
            value = config.app,
            onValueChange = { onConfigChange(config.copy(app = it)) }
        )
    }
}

@Composable
@Preview
private fun ConfigScreenPreview() {
    ConfigScreenContent(
        scope = rememberCoroutineScope(),
        configState = ConfigUiState.Success(targets = listOf(EMU_SAMPLE_TARGET, EMU_SAMPLE_TARGET)),
        onCloseClick = {},
        onAdditionClick = {},
        onRemoveClick = {},
        onTargetUpdate = { _,_ -> },
    )
}
