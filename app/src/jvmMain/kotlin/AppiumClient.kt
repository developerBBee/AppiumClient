import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import screen.config.ConfigScreen
import screen.main.MainScreen

@Composable
@Preview
private fun App() {
    MaterialTheme {
        var showConfig by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                onConfigClick = { showConfig = true }
            )

            if (showConfig) {
                ConfigScreen(
                    modifier = Modifier.fillMaxSize(),
                    onOutsideClick = { showConfig = false },
                    onCloseClick = { showConfig = false },
                )
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Appium Client",
        icon = rememberVectorPainter(Icons.Default.Star),
        state = rememberWindowState(size = DpSize(1280.dp, 720.dp)),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
