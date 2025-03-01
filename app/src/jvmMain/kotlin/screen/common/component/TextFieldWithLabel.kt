package screen.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun TextFieldWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    value: String,
    validator: (String) -> Boolean = { true },
    onValueChange: (String) -> Unit,
) {
    ContentWithLabel(
        modifier = modifier,
        label = label,
        textStyle = textStyle
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            singleLine = true,
            textStyle = textStyle,
            onValueChange = {
                if (validator(it)) {
                    onValueChange(it)
                }
            }
        )
    }
}
