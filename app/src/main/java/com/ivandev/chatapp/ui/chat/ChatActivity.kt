package com.ivandev.chatapp.ui.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.ivandev.chatapp.R
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_ID
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_IMAGE
import com.ivandev.chatapp.model.ACTIVITY_RECEIVER_NAME
import com.ivandev.chatapp.model.Chat
import com.ivandev.chatapp.ui.components.MessageReceived
import com.ivandev.chatapp.ui.components.MessageSent
import com.ivandev.chatapp.ui.components.Users
import com.ivandev.chatapp.ui.theme.ChatTheme
import com.ivandev.chatapp.ui.theme.PrimaryColor
import com.ivandev.chatapp.ui.theme.TextColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : ComponentActivity(), EventListener<QuerySnapshot> {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: ChatViewModel by viewModels()
    private var chatMessages by mutableStateOf<List<Chat>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receiverId = intent.getStringExtra(ACTIVITY_RECEIVER_ID)!!
        val receiverName = intent.getStringExtra(ACTIVITY_RECEIVER_NAME)!!
        val receiverImage = intent.getStringExtra(ACTIVITY_RECEIVER_IMAGE)!!
        val currentUserUid = firebaseAuth.currentUser?.uid!!
        viewModel.listenMessages(currentUserUid, receiverId, this)
        setContent {
            ChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryColor
                ) {
                    App(receiverId, receiverName, receiverImage)
                }
            }
        }
    }

    @Composable
    fun App(id: String, name:String, image:String) {
        val currenUserUid = firebaseAuth.currentUser?.uid
        var message by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val lazyColumnState = rememberLazyListState()

        LaunchedEffect(chatMessages) {
            coroutineScope.launch {
                lazyColumnState.scrollToItem(Int.MAX_VALUE)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PrimaryColor)
        ) {
            TopAppBar(modifier = Modifier.fillMaxWidth(), backgroundColor = PrimaryColor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Users(
                        userName = name,
                        imageProfile = image,
                        modifier = Modifier.fillMaxWidth(),
                        imageContentDescription = getString(R.string.image_content_description)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 5.dp, bottom = 5.dp), state = lazyColumnState) {
                    items(chatMessages.size) {
                        val currentItem = chatMessages[it]
                        if (it != chatMessages.size - 1) {
                            val nextItem = chatMessages[it + 1]
                            if (currentItem.receiverId == currenUserUid) {
                                MessageReceived(text = currentItem.message)
                                if (nextItem.receiverId != currenUserUid) {
                                    Spacer(
                                        modifier = Modifier
                                            .height(30.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            } else {
                                MessageSent(text = currentItem.message)
                                if (nextItem.senderId != currenUserUid) {
                                    Spacer(
                                        modifier = Modifier
                                            .height(30.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            if (currentItem.receiverId == currenUserUid) {
                                MessageReceived(text = currentItem.message)
                            } else {
                                MessageSent(text = currentItem.message)
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = message,
                    textStyle = TextStyle(fontSize = 14.sp, color = TextColor),
                    onValueChange = { message = it },
                    label = { Text(text = getString(R.string.message), style = TextStyle(color = Color.LightGray)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 8.dp, top = 2.dp, bottom = 8.dp, end = 8.dp),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Go
                    ),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (message.trim().isEmpty()) return@IconButton
                            viewModel.sendMessage(currenUserUid!!, id, message.trim())
                            message = ""
                        }) {
                            val painter = painterResource(id = R.drawable.ic_send_message)
                            Icon(
                                painter = painter,
                                contentDescription = getString(R.string.send_message_description),
                                tint = Color.LightGray
                            )
                        }
                    }, colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.DarkGray,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = TextColor
                    )
                )
            }
        }
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return
        value?.documentChanges?.forEach { document ->
            if (document.type == DocumentChange.Type.ADDED) {
                val chat = viewModel.getChatModel(document)
                chatMessages = chatMessages.plus(chat)
            }
        }
        chatMessages = chatMessages.sortedBy { it.date }
    }
}