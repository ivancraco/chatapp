package com.ivandev.chatapp.ui.user

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.ivandev.chatapp.R
import com.ivandev.chatapp.data.cipher.Decode
import com.ivandev.chatapp.data.cipher.Encode
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_ID
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_IMAGE
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_NAME
import com.ivandev.chatapp.model.User
import com.ivandev.chatapp.ui.chat.ChatActivity
import com.ivandev.chatapp.ui.components.CurrentUser
import com.ivandev.chatapp.ui.components.Users
import com.ivandev.chatapp.ui.signin.SignInActivity
import com.ivandev.chatapp.ui.theme.ChatTheme
import com.ivandev.chatapp.ui.theme.Green
import com.ivandev.chatapp.ui.theme.PrimaryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : ComponentActivity(), EventListener<QuerySnapshot> {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: UserViewModel by viewModels()
    private var imageBitmap by mutableStateOf<ImageBitmap?>(null)
    private var currentUser by mutableStateOf<User?>(null)
    private var users by mutableStateOf<List<User>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth.currentUser?.let { firebaseUser ->
            viewModel.getUsers(firebaseUser.uid, this)
        }
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
    fun User(currentUser: User, users: List<User>) {
        if (imageBitmap == null) {
            imageBitmap = if (currentUser.image.isNotEmpty()) {
                val decodeImage = Decode.decodeImage(currentUser.image)
                decodeImage
            } else {
                val image = BitmapFactory.decodeResource(
                    LocalContext.current.resources,
                    R.drawable.profile_image
                )
                image.asImageBitmap()
            }
        }
        Column(
            Modifier
                .background(color = PrimaryColor)
                .fillMaxSize()
        ) {
            TopAppBar(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.Black) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CurrentUser(
                        user = currentUser,
                        modifier = Modifier.weight(1f),
                        imageBitmap = imageBitmap!!,
                        buttonMessage = getString(R.string.sign_out),
                        selectImage = {
                            selectImage()
                        },
                        onClickSignOut = {
                            firebaseAuth.signOut()
                            startActivity(Intent(this@UserActivity, SignInActivity::class.java))
                            finish()
                        })
                }
            }
            LazyColumn(
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            ) {
                items(users) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp)
                            .clickable {
                                startChatActivity(it)
                            },
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Users(
                            userName = it.userName,
                            imageProfile = it.image,
                            modifier = Modifier.weight(1f),
                            imageContentDescription = getString(R.string.image_content_description)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    private fun startChatActivity(chatUser: User) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ACTIVITY_RECEIVER_ID, chatUser.id)
        intent.putExtra(ACTIVITY_RECEIVER_NAME, chatUser.userName)
        intent.putExtra(ACTIVITY_RECEIVER_IMAGE, chatUser.image)
        startActivity(intent)
    }

    @Composable
    fun App() {
        currentUser?.let {
            User(it, users)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(intent)
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    val imageUri = it.data!!.data
                    try {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageBitmap = bitmap.asImageBitmap()
                        val imageEncode = Encode.encodeImage(bitmap = bitmap)
                        val currentUserUid = firebaseAuth.currentUser?.uid!!
                        viewModel.addImageToFirebase(currentUserUid, imageEncode)
                    } catch (fileNotFoundException: FileNotFoundException) {
                        Log.d("IvanDev", fileNotFoundException.stackTrace.toString())
                    }
                }
            }
        }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        value?.documentChanges?.forEach { documentChange ->
            if (documentChange.type == DocumentChange.Type.ADDED) {
                val user = viewModel.getUserModel(documentChange)
                val currentUserUid = firebaseAuth.currentUser?.uid!!
                if (user.id == currentUserUid) {
                    currentUser = user
                } else {
                    users = users.plus(user)
                }
            }
        }
    }
}

