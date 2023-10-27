package com.nexis.acilyardim.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.viewmodel.base.BaseViewModel

class SignInViewModel(application: Application) : BaseViewModel(application) {
    val signedUserId = MutableLiveData<String>()

    fun signInUser(userEmail: String, userPassword: String){
        FirebaseUtil.mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                FirebaseUtil.fUser = it.result.user

                if (it.isSuccessful) {
                    successMessage.value = "Başarıyla giriş yaptınız"
                    signedUserId.value = FirebaseUtil.fUser?.uid
                }
                else
                    errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
            }
    }

    fun checkSigned(){
        FirebaseUtil.fUser = FirebaseUtil.mAuth.currentUser
        signedUserId.value = FirebaseUtil.fUser?.uid
    }
}