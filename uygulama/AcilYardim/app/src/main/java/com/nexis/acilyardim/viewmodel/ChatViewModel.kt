package com.nexis.acilyardim.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.nexis.acilyardim.model.Channel
import com.nexis.acilyardim.model.Chat
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.viewmodel.base.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var chatId: String
    private lateinit var chatData: HashMap<String, Any>
    private lateinit var chats: ArrayList<Chat>

    val channelId = MutableLiveData<String?>()
    val sendedState = MutableLiveData<Boolean>()
    val chatList = MutableLiveData<ArrayList<Chat>>()

    fun checkChannel(userId: String, targetId: String){
        FirebaseUtil.mDocRef = FirebaseUtil.mFireStore.collection("Users")
            .document(userId).collection("Chat Channels").document(targetId)

        FirebaseUtil.mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    AppUtil.mChannel = it.toObject(Channel::class.java)!!
                    channelId.value = AppUtil.mChannel.channelId
                } else {
                    FirebaseUtil.mDocRef = FirebaseUtil.mFireStore.collection("Users")
                        .document(targetId).collection("Chat Channels").document(userId)

                    FirebaseUtil.mDocRef.get()
                        .addOnSuccessListener {
                            if (it.exists()){
                                AppUtil.mChannel = it.toObject(Channel::class.java)!!
                                val tempChannel: Channel = Channel(AppUtil.mChannel.channelId, targetId)

                                FirebaseUtil.mFireStore.collection("Users").document(userId)
                                    .collection("Chat Channels").document(targetId).set(tempChannel)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful)
                                            channelId.value = AppUtil.mChannel.channelId
                                        else
                                            errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
                                    }
                            } else
                                channelId.value = null
                        }.addOnFailureListener {
                            errorMessage.value = "Bir hata oluştu: ${it.message}"
                        }
                }
            }.addOnFailureListener {
                errorMessage.value = "Bir hata oluştu: ${it.message}"
            }
    }

    fun createChannel(userId: String, targetId: String){
        val chnlId: String = UUID.randomUUID().toString()
        AppUtil.mChannel = Channel(chnlId, targetId)

        FirebaseUtil.mFireStore.collection("Users").document(userId)
            .collection("Chat Channels").document(targetId).set(AppUtil.mChannel)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    AppUtil.mChannel = Channel(chnlId, userId)

                    FirebaseUtil.mFireStore.collection("Users").document(targetId)
                        .collection("Chat Channels").document(userId).set(AppUtil.mChannel)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                channelId.value = chnlId
                            else
                                errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
                        }
                } else
                    errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
            }
    }

    fun sendMessage(messageContent: String, senderId: String, senderName: String, receiverId: String, channelId: String, messageType: String){
        chatId = UUID.randomUUID().toString()

        chatData = HashMap()
        chatData.put("chatId", chatId)
        chatData.put("messageContent", messageContent)
        chatData.put("messageDate", FieldValue.serverTimestamp())
        chatData.put("messageDateString", AppUtil.getFullDateWithStringByTimeZone())
        chatData.put("messageType", messageType)
        chatData.put("receiverId", receiverId)
        chatData.put("senderId", senderId)
        chatData.put("senderName", senderName)

        FirebaseUtil.mFireStore.collection("Chats").document(channelId)
            .collection("Messages").document(chatId).set(chatData)
            .addOnCompleteListener {
                sendedState.value = it.isSuccessful

                if (!it.isSuccessful)
                    errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
            }
    }

    fun getChats(channelId: String, userId: String){
        FirebaseUtil.mQuery = FirebaseUtil.mFireStore.collection("Chats")
            .document(channelId).collection("Messages").orderBy("messageDate", Query.Direction.ASCENDING)

        FirebaseUtil.mQuery.addSnapshotListener { value, error ->
            if (error != null){
                errorMessage.value = error.message
                return@addSnapshotListener
            }

            chats = ArrayList()

            if (value != null){
                if (value.documents.size > 0){
                    for (c in value.documents.indices){
                        if (value.documents[c].exists()){
                            AppUtil.mChat = value.documents[c].toObject(Chat::class.java)!!
                            chats.add(AppUtil.mChat)

                            if (c == (value.documents.size - 1))
                                chatList.value = chats
                        } else {
                            if (c == (value.documents.size - 1))
                                chatList.value = chats
                        }
                    }
                }
            }
        }
    }
}