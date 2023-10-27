package com.nexis.acilyardim.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userCreatedDate: String = "",
    val userProfileUrl: String = "",
    val userIsOnline: Boolean = false,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0
) : Parcelable
