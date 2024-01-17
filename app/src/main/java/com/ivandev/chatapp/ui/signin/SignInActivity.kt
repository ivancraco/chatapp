package com.ivandev.chatapp.ui.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.ivandev.chatapp.R
import com.ivandev.chatapp.ui.components.ChatButton
import com.ivandev.chatapp.ui.components.ChatTextField
import com.ivandev.chatapp.ui.components.PasswordTextField
import com.ivandev.chatapp.ui.ex.toast
import com.ivandev.chatapp.ui.signup.SignUpActivity
import com.ivandev.chatapp.ui.theme.ChatTheme
import com.ivandev.chatapp.ui.theme.Green
import com.ivandev.chatapp.ui.theme.PrimaryColor
import com.ivandev.chatapp.ui.user.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        setContent {
            ChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryColor
                ) {
                    App()
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(
            text = name,
            style = TextStyle(color = Green, fontWeight = FontWeight.Bold, fontSize = 30.sp)
        )
    }

    @Composable
    fun App() {
        checkUserIsVerified()
        var email = ""
        var password = ""
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
                .padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting(getString(R.string.greeting))
            Text(
                text = getString(R.string.log_in_to_continue_message),
                style = TextStyle(color = Color.Gray, fontSize = 12.sp),
                modifier = Modifier.padding(8.dp)
            )
            ChatTextField(
                hint = getString(R.string.email),
                keyboardType = KeyboardType.Email,
                onTextListener = {
                    email = it
                })
            PasswordTextField(text = getString(R.string.password), onTextListener = {
                password = it
            })
            ChatButton(hint = getString(R.string.sign_in), onClick = {
                if (email.trim().isEmpty() || password.trim().isEmpty()) return@ChatButton
                signInWithEmailAndPassword(email, password)
            })
            ClickableText(
                text = AnnotatedString(text = getString(R.string.create_new_account_message)),
                onClick = { singUp() },
                style = TextStyle(color = Color.Gray, fontSize = 12.sp)
            )
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if (!firebaseAuth.currentUser?.isEmailVerified!!) {
                    toast(getString(R.string.verify_email_message))
                } else {
                    startActivity(Intent(this@SignInActivity, UserActivity::class.java))
                    finish()
                }
            } else {
                toast(getString(R.string.sign_in_error_message), length = Toast.LENGTH_LONG)
            }
        }
    }

    private fun checkUserIsVerified() {
        if (firebaseAuth.currentUser != null &&
            firebaseAuth.currentUser!!.isEmailVerified
        ) {
            startActivity(Intent(this, UserActivity::class.java))
            finish()
        } else if (firebaseAuth.currentUser != null &&
            !firebaseAuth.currentUser!!.isEmailVerified
        ) {
            firebaseAuth.signOut()
        }
    }

    private fun singUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}

