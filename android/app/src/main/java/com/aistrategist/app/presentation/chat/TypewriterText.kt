package com.aistrategist.app.presentation.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    delayMillis: Long = 15L // Speed of typing
) {
    val displayedText = remember { mutableStateOf("") }

    LaunchedEffect(text) {
        // Reset if new totally different text is passed (or handle streaming chunks if appending)
        displayedText.value = ""
        for (i in text.indices) {
            displayedText.value = text.substring(0, i + 1)
            delay(delayMillis)
        }
    }

    Text(
        text = displayedText.value,
        modifier = modifier,
        style = style
    )
}
