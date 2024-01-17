package com.ivandev.chatapp.data.firebase

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.ivandev.chatapp.data.cipher.Decode
import com.ivandev.chatapp.data.cipher.Encode
import com.ivandev.chatapp.model.*
import java.util.*
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) {

    fun sendMessage(currentUser: String, receiver: String, message: String) {
        firebaseFirestore.collection(FIREBASE_CHATS)
            .add(
                hashMapOf(
                    CHAT_SENDER_ID to currentUser,
                    CHAT_RECEIVER_ID to receiver,
                    CHAT_MESSAGE to Encode.encodeMessage(message, CIPHER_PASSWORD),
                    CHAT_DATE to Date().time
                )
            )
    }

    fun listenMessages(currentUser: String, receiver: String, listener: EventListener<QuerySnapshot>) {
        firebaseFirestore.collection(FIREBASE_CHATS)
            .whereEqualTo(CHAT_SENDER_ID, currentUser)
            .whereEqualTo(CHAT_RECEIVER_ID, receiver)
            .addSnapshotListener(listener)
        firebaseFirestore.collection(FIREBASE_CHATS)
            .whereEqualTo(CHAT_SENDER_ID, receiver)
            .whereEqualTo(CHAT_RECEIVER_ID, currentUser)
            .addSnapshotListener(listener)
    }

    fun getChatModel(document: DocumentChange): Chat {
        val sender = document.document.getString(CHAT_SENDER_ID)!!
        val receiver = document.document.getString(CHAT_RECEIVER_ID)!!
        var message = document.document.getString(CHAT_MESSAGE)!!
        message = Decode.decodeMessage(message, CIPHER_PASSWORD)
        val date = document.document.getLong(CHAT_DATE)!!
        return Chat(sender, receiver, message, date)
    }


    fun createAccount(userName: String, uid: String) {
        firebaseFirestore
            .collection(FIREBASE_USERS)
            .document(uid)
            .set(
                hashMapOf(
                    USER_ID to uid,
                    USER_NAME to userName,
                    USER_IMAGE to ""
                )
            )
    }

    fun getUsers(currentUserUid: String, listener: EventListener<QuerySnapshot>) {
        firebaseFirestore.collection(FIREBASE_USERS)
            .whereEqualTo(USER_ID, currentUserUid)
            .addSnapshotListener(listener)
        firebaseFirestore.collection(FIREBASE_USERS)
            .whereNotEqualTo(USER_ID, currentUserUid)
            .addSnapshotListener(listener)
    }

    fun addImageToFirebase(currentUserUid: String, imageEncode: String) {
        firebaseFirestore.collection(FIREBASE_USERS)
            .document(currentUserUid)
            .update(USER_IMAGE, imageEncode)
    }

    companion object {
        private const val CIPHER_PASSWORD = "Cthulhu"
    }
}