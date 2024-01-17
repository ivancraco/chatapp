package com.ivandev.chatapp.ui.user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.ivandev.chatapp.data.firebase.FirestoreService
import com.ivandev.chatapp.model.USER_ID
import com.ivandev.chatapp.model.USER_IMAGE
import com.ivandev.chatapp.model.USER_NAME
import com.ivandev.chatapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {

    fun getUsers(currentUserUid: String, listener: EventListener<QuerySnapshot>) {
        firestoreService.getUsers(currentUserUid, listener)
    }

    fun getUserModel(documentChange: DocumentChange): User {
        val id = documentChange.document.getString(USER_ID)!!
        val userName = documentChange.document.getString(USER_NAME)!!
        val image = documentChange.document.getString(USER_IMAGE)!!
        return User(id, userName, image)
    }

    fun addImageToFirebase(currentUserUid: String, imageEncode: String) {
        firestoreService.addImageToFirebase(currentUserUid, imageEncode)
    }
}