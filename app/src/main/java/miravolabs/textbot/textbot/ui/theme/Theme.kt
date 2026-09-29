package miravolabs.textbot.textbot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = Emerald,
    tertiary = Amber,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = Emerald,
    tertiary = Amber,
)

@Composable
fun TextBotTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}