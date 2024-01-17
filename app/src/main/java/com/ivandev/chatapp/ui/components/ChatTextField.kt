package com.ivandev.chatapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivandev.chatapp.ui.theme.TextColor

@Composable
fun ChatTextField(
    hint: String,
    fontSize: TextUnit = 12.sp,
    padding: Dp = 5.dp,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTextListener: ((str: String) -> Unit) = {},
    isError: Boolean = false
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextListener(text)
        },
        label = { Text(text = hint, style = TextStyle(color = Color.LightGray)) },
        maxLines = 1,
        isError = isError,
        textStyle = TextStyle(fontSize = fontSize, color = TextColor),
        modifier = Modifier.padding(padding),
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = TextColor,
            backgroundColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun PasswordTextField(
    text: String,
    fontSize: TextUnit = 12.sp,
    padding: Dp = 5.dp,
    onTextListener: ((str: String) -> Unit) = {}
) {
    var hidden by remember { mutableStateOf(true) }
    val visualTransformation =
        if (hidden) PasswordVisualTransformation() else VisualTransformation.None

    ChatTextField(
        hint = text,
        fontSize = fontSize,
        padding = padding,
        visualTransformation = visualTransformation,
        trailingIcon = {
            IconButton(onClick = { hidden = !hidden }) {
                val painter = painterResource(
                    if (hidden) com.ivandev.chatapp.R.drawable.ic_visibility_off
                    else com.ivandev.chatapp.R.drawable.ic_visibility_on
                )
                val contentDescription = if (hidden) "Show password" else "Hidde password"
                Icon(painter = painter, contentDescription = contentDescription, tint = Color.LightGray)
            }
        }, onTextListener = onTextListener)

}
