package com.nexis.acilyardim.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nexis.acilyardim.model.Chat
import com.nexis.acilyardim.model.User

object FirebaseUtil {
    val mFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorage: StorageReference = FirebaseStorage.getInstance().reference
    var fUser: FirebaseUser? = null
    lateinit var mQuery: Query
    lateinit var mDocRef: DocumentReference
    private lateinit var sRef: StorageReference
    private lateinit var rRef: StorageReference

    fun getUserData(userId: String, getUserDataOnComplete: (userData: User?) -> Unit){
        mDocRef = mFireStore.collection("Users").document(userId)

        mDocRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    AppUtil.mUser = it.toObject(User::class.java)!!
                    getUserDataOnComplete(AppUtil.mUser)
                }
            }.addOnFailureListener {
                getUserDataOnComplete(null)
            }
    }

    fun getLastChat(channelId: String, userId: String, getLastChatOnComplete: (chatData: Chat?) -> Unit){
        mQuery = mFireStore.collection("Chats").document(channelId)
            .collection("Messages").orderBy("messageDate", Query.Direction.DESCENDING).limit(1)

        mQuery.get().addOnSuccessListener {
            if (it.documents.size > 0){
                if (it.documents[0].exists()){
                    AppUtil.mChat = it.documents[0].toObject(Chat::class.java)!!
                    getLastChatOnComplete(AppUtil.mChat)
                } else
                    getLastChatOnComplete(null)
            } else
                getLastChatOnComplete(null)
        }.addOnFailureListener {
            getLastChatOnComplete(null)
        }
    }
}