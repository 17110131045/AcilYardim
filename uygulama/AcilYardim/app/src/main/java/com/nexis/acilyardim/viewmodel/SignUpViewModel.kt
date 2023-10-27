package com.nexis.acilyardim.viewmodel

import android.app.Application
import com.google.android.gms.maps.model.LatLng
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.util.Singleton
import com.nexis.acilyardim.viewmodel.base.BaseViewModel

class SignUpViewModel(application: Application) : BaseViewModel(application) {
    private lateinit var userId: String
    private lateinit var userCreatedDate: String

    fun signUpUser(userEmail: String, userPassword: String, userName: String, lastUserLocation: LatLng){
        FirebaseUtil.mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    FirebaseUtil.fUser = it.result.user

                    if (FirebaseUtil.fUser != null){
                        userId = FirebaseUtil.fUser!!.uid
                        userCreatedDate = AppUtil.getFullDateWithStringByTimeZone()

                        AppUtil.mUser = User(
                            userId,
                            userName,
                            userEmail,
                            userCreatedDate,
                            Singleton.defaultProfileUrl,
                            false,
                            lastUserLocation.latitude,
                            lastUserLocation.longitude
                        )

                        FirebaseUtil.mFireStore.collection("Users").document(userId)
                            .set(AppUtil.mUser)
                            .addOnCompleteListener {
                                if (it.isSuccessful) successMessage.value = "Başarıyla kayıt oldunuz"
                                else errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
                            }
                    }
                } else
                    errorMessage.value = "Bir hata oluştu: ${it.exception?.message}"
            }
    }
}