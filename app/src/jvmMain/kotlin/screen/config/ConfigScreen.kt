package screen.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import data.AppiumConfiguration
import data.Target
import data.senario.ScenarioName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.component.indicator.KodeeProgressIndicator

@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    onOutsideClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onOutsideClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        ConfigScreenContent(
            modifier = Modifier
                .fillMaxSize(fraction = 0.9f)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .clickable(enabled = false) {},
            scope = scope,
            onCloseClick = onCloseClick,
        )
    }
}

@Composable
fun ConfigScreenContent(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    onCloseClick: () -> Unit,
    configViewModel: ConfigViewModel = viewModel(),
) {
    val configState by configViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = configState) {
            is ConfigUiState.Loading -> {
                KodeeProgressIndicator()
            }

            is ConfigUiState.Success -> {
                TargetsTable(
                    modifier = Modifier.fillMaxSize(),
                    scope = scope,
                    targets = state.targets,
                    onAdditionClick = configViewModel::addNewTarget,
                    onRemoveClick = configViewModel::removeTarget,
                    onTargetUpdate = configViewModel::updateTarget,
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

                    EditScenario(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        scenarioName = target.scenarioName,
                        onScenarioChange = { onTargetUpdate(page, target.copy(scenarioName = it)) },
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
//        TextFieldWithLabel(
//            label = "Path",
//            value = config.path,
//            onValueChange = { onConfigChange(config.copy(path = it)) }
//        )
//        CheckBoxWithLabel(
//            label = "Enable SSL",
//            checked = config.sslEnabled,
//            onCheckedChange = { onConfigChange(config.copy(sslEnabled = it)) }
//        )
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
private fun EditScenario(
    modifier: Modifier = Modifier,
    scenarioName: ScenarioName,
    onScenarioChange: (ScenarioName) -> Unit,
) {
    ContentWithLabel(
        modifier = modifier,
        label = "シナリオ",
        textStyle = MaterialTheme.typography.body2
    ) {
        val dropdownState = remember { DropdownMenuState() }

        Box(modifier = modifier) {
            OutlinedTextField(
                enabled = false,
                value = scenarioName.value,
                onValueChange = {},
                modifier = Modifier.clickable {
                    dropdownState.status = DropdownMenuState.Status.Open(position = Offset(x = 0f, y = 0f))
                }
            )

            DropdownMenu(state = dropdownState) {
                ScenarioName.entries.forEach { scenarioName ->
                    DropdownMenuItem(
                        onClick = {
                            dropdownState.status = DropdownMenuState.Status.Closed
                            onScenarioChange(scenarioName)
                        }
                    ) {
                        Text(text = scenarioName.value)
                    }
                }
            }
        }
    }
}

@Composable
private fun TextFieldWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    validator: (String) -> Boolean = { true },
    onValueChange: (String) -> Unit,
) {
    var text by remember { mutableStateOf(value) }

    ContentWithLabel(
        modifier = modifier,
        label = label,
        textStyle = MaterialTheme.typography.body2
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            singleLine = true,
            textStyle = MaterialTheme.typography.body2,
            onValueChange = {
                if (validator(it)) {
                    text = it
                    onValueChange(it)
                }
            }
        )
    }
}

@Composable
private fun ContentWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = textStyle,
        )
        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.CenterStart
        ) {
            content()
        }
    }
}

//@Composable
//private fun CheckBoxWithLabel(
//    label: String,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit
//) {
//    var boxChecked by remember { mutableStateOf(checked) }
//
//    Row(
//        modifier = Modifier
//            .padding(horizontal = 32.dp, vertical = 8.dp)
//            .clip(shape = RoundedCornerShape(32.dp))
//            .clickable { boxChecked = !boxChecked },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Checkbox(
//            modifier = Modifier.height(32.dp),
//            checked = boxChecked,
//            onCheckedChange = onCheckedChange
//        )
//        Text(
//            modifier = Modifier.padding(end = 16.dp),
//            text = label,
//            style = MaterialTheme.typography.body2,
//        )
//    }
//}

@Composable
@Preview
private fun ConfigScreenPreview() {
    ConfigScreen(
        modifier = Modifier.fillMaxSize(),
        scope = rememberCoroutineScope(),
        onOutsideClick = {},
        onCloseClick = {},
    )
}
