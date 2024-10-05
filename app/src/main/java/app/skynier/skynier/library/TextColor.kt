package app.skynier.skynier.library

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.skynier.skynier.ui.theme.Green
import app.skynier.skynier.ui.theme.Red
import app.skynier.skynier.ui.theme.Blue

@Composable
fun textColor(textColorSetting: Int, categoryId: Int): Color {
    return when (textColorSetting) {
        0 -> when (categoryId) {
            1 -> Red
            2 -> Green
            3 -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.onBackground
        }
        1 -> when (categoryId) {
            1 -> Green
            2 -> Red
            3 -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.onBackground
        }
        2 -> when (categoryId) {
            1 -> Red
            2 -> Blue
            3 -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.onBackground
        }
        3 -> when (categoryId) {
            1 -> Blue
            2 -> Red
            3 -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.onBackground
        }
        else -> MaterialTheme.colorScheme.onBackground
    }
}