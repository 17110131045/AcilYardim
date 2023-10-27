package com.nexis.acilyardim.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.viewmodel.base.BaseViewModel

class ProfileViewModel(application: Application) : BaseViewModel(application) {
    val userData = MutableLiveData<User>()

    fun getUserData(userId: String){
        FirebaseUtil.getUserData(userId){ userData ->
            userData?.let {
                this.userData.value = it
            }
        }
    }
}