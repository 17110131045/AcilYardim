package com.nexis.acilyardim.model

data class Chat(
    val chatId: String = "",
    val messageContent: String = "",
    val messageDateString: String = "",
    val messageType: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val senderName: String = ""
)
