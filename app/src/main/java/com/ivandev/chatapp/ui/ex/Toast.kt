package com.ivandev.chatapp.ui.ex

import android.widget.Toast
import androidx.activity.ComponentActivity
import com.ivandev.chatapp.R
import io.github.muddz.styleabletoast.StyleableToast

fun ComponentActivity.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    StyleableToast.makeText(this, text, length, R.style.styleableToast).show()
}