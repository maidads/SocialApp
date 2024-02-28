package com.example.androidprojectma23

import com.google.firebase.Timestamp

data class ChatMessage(
    val userId: String,
    val userName: String,
    val messageBody: String,
    val messageTime: Timestamp,
    val profileImageUrl: String,
    val isUserMessage: Boolean
)
