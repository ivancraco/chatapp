package com.ivandev.chatapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivandev.chatapp.ui.theme.Green
import com.ivandev.chatapp.ui.theme.TextColor

@Composable
fun MessageSent(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp, end = 6.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(2.dp)
                .background(color = Green, shape = RoundedCornerShape(10.dp))
                .border(2.dp, Green, RoundedCornerShape(10.dp))
        ) {
            Text(text = text, modifier = Modifier.wrapContentSize().padding(10.dp), style = TextStyle(color = TextColor, fontSize = 14.sp))
        }
    }
}