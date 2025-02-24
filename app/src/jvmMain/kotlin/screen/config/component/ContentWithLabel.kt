package screen.config.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun ContentWithLabel(
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
