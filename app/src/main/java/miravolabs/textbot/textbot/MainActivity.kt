package miravolabs.textbot.textbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import miravolabs.textbot.textbot.ui.theme.TextBotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextBotTheme {
                TextBotAppNavigation()
            }
        }
    }
}