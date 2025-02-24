import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import screen.config.ConfigScreen
import screen.diff.DiffScreen
import screen.main.MainScreen

@Composable
@Preview
private fun App() {
    MaterialTheme {
        var showConfig by rememberSaveable { mutableStateOf(false) }
        var showDiff by rememberSaveable { mutableStateOf(false) }
        val scope: CoroutineScope = rememberCoroutineScope()

        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                MainScreen(
                    modifier = Modifier.fillMaxSize(),
                    scope = scope,
                    onConfigClick = { showConfig = true },
                    onDiffClick = { showDiff = true },
                )

                if (showConfig) {
                    ConfigScreen(
                        scope = scope,
                        onOutsideClick = { showConfig = false },
                        onCloseClick = { showConfig = false },
                    )
                }

                if (showDiff) {
                    DiffScreen(
                        onOutsideClick = { showDiff = false },
                        onCloseClick = { showDiff = false },
                    )
                }
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Appium Client",
        icon = rememberVectorPainter(Icons.Default.Park),
        state = rememberWindowState(size = DpSize(1280.dp, 720.dp)),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
