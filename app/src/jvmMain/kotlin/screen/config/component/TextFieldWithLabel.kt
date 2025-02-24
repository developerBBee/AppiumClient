package screen.config.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TextFieldWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    validator: (String) -> Boolean = { true },
    onValueChange: (String) -> Unit,
) {
    var text by remember { mutableStateOf(value) }

    ContentWithLabel(
        modifier = modifier,
        label = label,
        textStyle = MaterialTheme.typography.body2
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            singleLine = true,
            textStyle = MaterialTheme.typography.body2,
            onValueChange = {
                if (validator(it)) {
                    text = it
                    onValueChange(it)
                }
            }
        )
    }
}
