package ui.screen.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ContentWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle,
    contentWeight: Float = 3f,
    contentsMargin: Dp = 0.dp,
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
        Spacer(modifier = Modifier.width(contentsMargin))
        Box(
            modifier = Modifier.weight(contentWeight),
            contentAlignment = Alignment.CenterStart
        ) {
            content()
        }
    }
}
