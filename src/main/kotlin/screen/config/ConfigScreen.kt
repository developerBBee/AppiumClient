package screen.config

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.AppiumConfiguration

@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier,
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
                .fillMaxSize(fraction = 0.8f)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .clickable(enabled = false) {},
            onCloseClick = onCloseClick,
        )
    }
}

@Composable
fun ConfigScreenContent(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    configViewModel: ConfigViewModel = rememberConfigViewModel(),
) {
    val configState by configViewModel.configStateFlow.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (val state = configState) {
            is ConfigState.Loading -> {
                CircularProgressIndicator()
            }

            is ConfigState.Success -> {
                EditConfigScreen(
                    modifier = Modifier.fillMaxSize(),
                    config = state.config,
                    onConfigChange = { configViewModel.saveSettings(it) },
                    onCloseClick = onCloseClick,
                )
            }

            is ConfigState.Error -> {
                Text(text = state.throwable.stackTraceToString())
            }
        }
    }
}

@Composable
private fun EditConfigScreen(
    modifier: Modifier = Modifier,
    config: AppiumConfiguration,
    onConfigChange: (AppiumConfiguration) -> Unit,
    onCloseClick: () -> Unit,
) {
    val verticalState = rememberScrollState(0)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(verticalState),
        ) {
            TextFieldWithLabel(
                label = "Host",
                value = config.host,
                onValueChange = { onConfigChange(config.copy(host = it)) }
            )
            TextFieldWithLabel(
                label = "Port",
                value = config.port.toString(),
                validator = {
                    val port = it.toIntOrNull()
                    (port != null && port in 0..65535)
                },
                onValueChange = { onConfigChange(config.copy(port = it.toInt())) }
            )
            TextFieldWithLabel(
                label = "Path",
                value = config.path,
                onValueChange = { onConfigChange(config.copy(path = it)) }
            )
            CheckBoxWithLabel(
                label = "Enable SSL",
                checked = config.sslEnabled,
                onCheckedChange = { onConfigChange(config.copy(sslEnabled = it)) }
            )
            TextFieldWithLabel(
                label = "UDID",
                value = config.udid,
                onValueChange = { onConfigChange(config.copy(udid = it)) }
            )
            TextFieldWithLabel(
                label = "AppFullPath",
                value = config.app,
                onValueChange = { onConfigChange(config.copy(app = it)) }
            )
        }
        OutlinedButton(onClick = onCloseClick) {
            Text(text = "閉じる")
        }
    }
}

@Composable
private fun TextFieldWithLabel(
    label: String,
    value: String,
    validator: (String) -> Boolean = { true },
    onValueChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
        )
        TextField(
            modifier = Modifier.weight(3f),
            value = text,
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
private fun CheckBoxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var boxChecked by remember { mutableStateOf(checked) }

    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .clip(shape = RoundedCornerShape(32.dp))
            .clickable { boxChecked = !boxChecked },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = boxChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            modifier = Modifier.padding(end = 16.dp),
            text = label
        )
    }
}

@Composable
private fun rememberConfigViewModel(): ConfigViewModel = rememberSaveable { ConfigViewModel() }
