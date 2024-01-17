package com.ivandev.chatapp.ui.chat

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.ivandev.chatapp.data.firebase.FirestoreService
import com.ivandev.chatapp.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    fun sendMessage(currentUser: String, receiver: String, message: String) {
        firestoreService.sendMessage(currentUser, receiver, message)
    }

    fun listenMessages(currentUser: String, receiver: String, listener: EventListener<QuerySnapshot>) {
        firestoreService.listenMessages(currentUser, receiver, listener)
    }

    fun getChatModel(document: DocumentChange): Chat {
        return firestoreService.getChatModel(document)
    }
}