package com.ivandev.chatapp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivandev.chatapp.R
import com.ivandev.chatapp.data.cipher.Decode
import com.ivandev.chatapp.ui.theme.TextColor

@Composable
fun Users(
    userName: String,
    imageProfile: String,
    modifier: Modifier,
    imageContentDescription: String
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    imageBitmap = if (imageProfile.isEmpty()){
        BitmapFactory.decodeResource(
            LocalContext.current.resources,
            R.drawable.profile_image).asImageBitmap()
    } else {
        Decode.decodeImage(imageProfile)
    }
    Image(
        bitmap = imageBitmap!!,
        contentDescription = imageContentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
    )
    Text(
        text = userName,
        modifier = Modifier.padding(start = 10.dp),
        style = TextStyle(fontSize = 13.sp, color = TextColor)
    )
    Spacer(modifier = modifier)
}