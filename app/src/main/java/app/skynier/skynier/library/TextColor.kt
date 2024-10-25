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

@Composable
fun textColorBasedOnAmount(textColorSetting: Int, amount: Double): Color {
    return when (textColorSetting) {
        0 -> if (amount >= 0) Green else Red
        1 -> if (amount >= 0) Red else Green
        2 -> if (amount >= 0) Blue else Red
        3 -> if (amount >= 0) Red else Blue
        else -> MaterialTheme.colorScheme.onBackground
    }
}