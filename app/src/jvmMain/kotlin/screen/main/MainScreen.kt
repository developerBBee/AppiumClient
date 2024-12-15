package screen.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onConfigClick: () -> Unit,
    mainViewModel: MainViewModel = rememberMainViewModel(),
) {

    DisposableEffect(Unit) {
        onDispose {
            mainViewModel.dispose()
        }
    }

    val scope = rememberCoroutineScope()

    val mainState by mainViewModel.mainStateFlow.collectAsState()

    val screenValue: MainScreenValue = when (val state = mainState) {
        MainState.Idle -> {
            MainScreenValue(onButtonClick = mainViewModel::run)
        }

        is MainState.Running -> {
            MainScreenValue(
                configEnabled = false,
                progress = true,
                message = state.log,
                buttonText = "キャンセル",
                onButtonClick = mainViewModel::cancel,
            )
        }

        is MainState.Cancelling -> {
            MainScreenValue(
                configEnabled = false,
                progress = true,
                message = state.log,
                buttonText = "キャンセル中",
            )
        }

        is MainState.Finished -> {
            MainScreenValue(
                message = "終了 [実行完了=${state.isCompletion}]",
                onButtonClick = mainViewModel::run,
            )
        }
        is MainState.Error -> {
            MainScreenValue(
                message = state.message,
                onButtonClick = mainViewModel::run,
            )
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Box(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (screenValue.progress) {
                        CircularProgressIndicator()
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
                            screenValue.onButtonClick()
                        }
                    }
                ) {
                    Text(
                        text = screenValue.buttonText,
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
                    text = screenValue.message,
                    style = MaterialTheme.typography.body1,
                )
            }
        }

        IconButton(
            enabled = screenValue.configEnabled,
            onClick = onConfigClick
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                tint = MaterialTheme.colors.primaryVariant,
                contentDescription = "設定"
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(modifier = Modifier.fillMaxSize(), onConfigClick = {})
}

@Composable
private fun rememberMainViewModel(): MainViewModel = rememberSaveable { MainViewModel() }

data class MainScreenValue(
    val configEnabled: Boolean = true,
    val progress: Boolean = false,
    val message: String = "",
    val buttonText: String = "実行",
    val onButtonClick: suspend () -> Unit = {},
)
