package screen.config.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle

@Composable
fun DropdownWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle = MaterialTheme.typography.body2,
    currentItemText: String,
    itemNames: List<String>,
    onSelectedIndex: (Int) -> Unit,
) {
    ContentWithLabel(
        modifier = modifier,
        label = label,
        textStyle = textStyle,
    ) {
        val dropdownState = remember { DropdownMenuState() }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                enabled = false,
                value = currentItemText,
                onValueChange = {},
                modifier = Modifier.clickable {
                    dropdownState.status = DropdownMenuState.Status.Open(position = Offset(x = 0f, y = 0f))
                }
            )

            DropdownMenu(
                modifier = Modifier.width(width = maxWidth),
                state = dropdownState
            ) {
                itemNames.forEachIndexed { index, name ->
                    DropdownMenuItem(
                        onClick = {
                            dropdownState.status = DropdownMenuState.Status.Closed
                            onSelectedIndex(index)
                        }
                    ) {
                        Text(text = name)
                    }
                }
            }
        }
    }
}
