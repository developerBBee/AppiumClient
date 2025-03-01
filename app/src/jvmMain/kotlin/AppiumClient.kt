import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.navigation.AppNavHost

@Composable
private fun App() {
    MaterialTheme {
        Surface {
            AppNavHost(modifier = Modifier.fillMaxSize())
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
