package com.nexis.acilyardim.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.acilyardim.model.HelpRequest
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.viewmodel.base.BaseViewModel

class HomeViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var helpRequestDate: String
    private lateinit var helpRequests: ArrayList<HelpRequest>

    val userData = MutableLiveData<User>()
    val helpRequestList = MutableLiveData<ArrayList<HelpRequest>>()

    fun getUserData(userId: String){
        FirebaseUtil.getUserData(userId){ userData ->
            this.userData.value = userData
        }
    }

    fun addNewHelpRequest(userId: String, helpRequestContent: String){
        helpRequestDate = AppUtil.getFullDateWithStringByTimeZone()
        AppUtil.mHelpRequest = HelpRequest(userId, helpRequestContent, helpRequestDate)

        FirebaseUtil.mFireStore.collection("Help Requests").document(userId)
            .set(AppUtil.mHelpRequest)
            .addOnCompleteListener {
                if (it.isSuccessful) successMessage.value = "Yardım isteği başarıyla iletildi"
                else errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
            }
    }

    fun getHelpRequests(){
        FirebaseUtil.mQuery = FirebaseUtil.mFireStore.collection("Help Requests")
        FirebaseUtil.mQuery.addSnapshotListener { value, error ->
            if (error != null){
                errorMessage.value = "Bir hata oluştu: ${error.message}"
                return@addSnapshotListener
            }

            if (value != null){
                if (value.documents.size > 0){
                    helpRequests = ArrayList()

                    for (hR in value.documents.indices){
                        if (value.documents[hR].exists()){
                            AppUtil.mHelpRequest = value.documents[hR].toObject(HelpRequest::class.java)!!
                            helpRequests.add(AppUtil.mHelpRequest)

                            if (hR == (value.documents.size - 1))
                                helpRequestList.value = helpRequests
                        } else {
                            if (hR == (value.documents.size - 1))
                                helpRequestList.value = helpRequests
                        }
                    }
                }
            }
        }
    }
}