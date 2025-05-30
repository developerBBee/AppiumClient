package ui.screen.common.component

import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ContentWithLabel(
        modifier = modifier,
        label = label,
        textStyle = textStyle,
        contentWeight = 0.2f,
        contentsMargin = 4.dp,
    ) {
        Checkbox(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}