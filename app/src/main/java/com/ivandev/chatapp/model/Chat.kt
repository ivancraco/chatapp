package com.ivandev.chatapp.model

data class Chat(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val date: Long
)
