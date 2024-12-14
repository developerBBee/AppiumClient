package screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onConfigClick: () -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        IconButton(onClick = onConfigClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                tint = MaterialTheme.colors.primaryVariant,
                contentDescription = "設定"
            )
        }

        OutlinedButton(
            modifier = Modifier
                .align(Alignment.Center)
                .width(160.dp)
                .height(64.dp),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "実行",
                style = MaterialTheme.typography.h4
            )
        }
    }
}
