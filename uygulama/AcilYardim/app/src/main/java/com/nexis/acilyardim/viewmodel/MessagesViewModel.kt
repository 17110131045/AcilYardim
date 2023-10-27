package com.nexis.acilyardim.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.acilyardim.model.Channel
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.viewmodel.base.BaseViewModel

class MessagesViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var channels: ArrayList<Channel>
    val channelList = MutableLiveData<ArrayList<Channel>>()

    fun getChannels(userId: String){
        FirebaseUtil.mQuery = FirebaseUtil.mFireStore.collection("Users")
            .document(userId).collection("Chat Channels")

        FirebaseUtil.mQuery.get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    channels = ArrayList()

                    for (c in it.documents.indices){
                        if (it.documents[c].exists()){
                            AppUtil.mChannel = it.documents[c].toObject(Channel::class.java)!!
                            channels.add(AppUtil.mChannel)

                            if (c == (it.documents.size - 1))
                                channelList.value = channels
                        } else {
                            if (c == (it.documents.size - 1))
                                channelList.value = channels
                        }
                    }
                }
            }.addOnFailureListener {
                errorMessage.value = "Bir hata oluştu: ${it.message}"
            }
    }
}