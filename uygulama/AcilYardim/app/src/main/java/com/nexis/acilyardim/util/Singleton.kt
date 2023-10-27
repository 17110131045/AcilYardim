package com.nexis.acilyardim.util

import android.annotation.SuppressLint
import android.view.View

class Singleton {
    companion object {
        val V_SIZE: Int = 40
        val H_SIZE: Int = 35
        var isCurrentMain: Boolean = true
        @SuppressLint("StaticFieldLeak")
        var isCurrentView: View? = null
        val defaultProfileUrl: String = "https://firebasestorage.googleapis.com/v0/b/acil-yardim-13687.appspot.com/o/default_profile.png?alt=media&token=670389e7-3338-40d1-b832-64bbe86123c1"
    }
}