package com.ivandev.chatapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivandev.chatapp.ui.theme.TextColor

@Composable
fun ChatButton(hint: String, onClick: () -> Unit = {}) {
    Button(onClick = onClick , modifier = Modifier.padding(5.dp)) {
        Text(text = hint, style = TextStyle(fontSize = 12.sp, color = TextColor, textAlign = TextAlign.Center))
    }
}