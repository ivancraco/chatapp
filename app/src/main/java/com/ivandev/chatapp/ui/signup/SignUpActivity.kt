package com.ivandev.chatapp.ui.signup

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.ivandev.chatapp.ui.components.ChatButton
import com.ivandev.chatapp.ui.components.ChatTextField
import com.ivandev.chatapp.ui.components.PasswordTextField
import com.ivandev.chatapp.ui.ex.toast
import com.ivandev.chatapp.ui.theme.ChatTheme
import com.ivandev.chatapp.ui.theme.Green
import com.ivandev.chatapp.ui.theme.PrimaryColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : ComponentActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryColor
                ) {
                    SignUp()
                }
            }
        }
    }

    @Composable
    fun SignUp() {
        var userName = ""
        var email = ""
        var password = ""
        var confirmPassword = ""
        val condition by viewModel.isVerified.collectAsState(initial = false)
        if (condition) {
            toast(getString(com.ivandev.chatapp.R.string.account_created_successfully))
            finish()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
                .padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getString(com.ivandev.chatapp.R.string.create_new_account_message),
                style = TextStyle(color = Green),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            ChatTextField(
                hint = getString(com.ivandev.chatapp.R.string.username),
                capitalization = KeyboardCapitalization.Sentences,
                onTextListener = {
                    userName = it
                })
            ChatTextField(
                hint = getString(com.ivandev.chatapp.R.string.email),
                keyboardType = KeyboardType.Email,
                onTextListener = {
                    email = it
                })
            PasswordTextField(
                text = getString(com.ivandev.chatapp.R.string.password),
                onTextListener = {
                    password = it
                })
            PasswordTextField(
                text = getString(com.ivandev.chatapp.R.string.confirm_password),
                onTextListener = {
                    confirmPassword = it
                })
            ChatButton(hint = getString(com.ivandev.chatapp.R.string.sign_up), onClick = {
                if (isValidData(userName, email, password, confirmPassword)) {
                    createUserWithEmailAndPassword(userName, email, password)
                }
            })
        }
    }

    private fun createUserWithEmailAndPassword(
        userName: String,
        email: String,
        password: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                user
                    ?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        toast(getString(com.ivandev.chatapp.R.string.send_email_verification))
                        viewModel.checkUserIsVerified(user)
                    }
                    ?.addOnFailureListener {
                        toast(getString(com.ivandev.chatapp.R.string.send_email_verification_error))
                    }
                viewModel.createAccount(userName, user?.uid!!)
            } else {
                if (it.exception is FirebaseAuthUserCollisionException) {
                    toast(
                        getString(
                            com.ivandev.chatapp.R.string.firebase_auth_user_collision_exception
                        ),
                        length = Toast.LENGTH_LONG
                    )
                } else if (it.exception is FirebaseAuthWeakPasswordException) {
                    toast(getString(com.ivandev.chatapp.R.string.firebase_auth_weak_password_exception))
                }
            }
        }
    }

    private fun isValidData(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (userName.trim().length > 20) {
            toast(getString(com.ivandev.chatapp.R.string.username_not_valid))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast(getString(com.ivandev.chatapp.R.string.email_not_valid))
            return false
        } else if (password != confirmPassword) {
            toast(getString(com.ivandev.chatapp.R.string.passwords_do_not_match))
            return false
        }
        return true
    }
}