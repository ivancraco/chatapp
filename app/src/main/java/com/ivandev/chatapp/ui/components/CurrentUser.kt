package com.ivandev.chatapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivandev.chatapp.model.User
import com.ivandev.chatapp.ui.theme.TextColor

@Composable
fun CurrentUser(
    user: User,
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    buttonMessage: String,
    selectImage: () -> Unit,
    onClickSignOut: () -> Unit) {
    Image(
        bitmap = imageBitmap,
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable {
                selectImage()
            }
    )
    Text(
        text = user.userName,
        modifier = Modifier.padding(start = 10.dp),
        style = TextStyle(fontSize = 13.sp, color = TextColor)
    )
    Spacer(modifier = modifier)
    ChatButton(hint = buttonMessage, onClick = {
        onClickSignOut()
    })
}