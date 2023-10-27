package com.nexis.acilyardim.util

import android.annotation.SuppressLint
import android.view.View
import com.nexis.acilyardim.model.Channel
import com.nexis.acilyardim.model.Chat
import com.nexis.acilyardim.model.HelpRequest
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.view.dialog.AddHelpRequestDialog
import com.nexis.acilyardim.view.dialog.HelpRequestDialog
import com.nexis.acilyardim.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    lateinit var mUser: User
    lateinit var mHelpRequest: HelpRequest
    lateinit var mChannel: Channel
    lateinit var mChat: Chat

    @SuppressLint("StaticFieldLeak")
    private lateinit var addHelpRequestDialog: AddHelpRequestDialog
    @SuppressLint("StaticFieldLeak")
    private lateinit var helpRequestDialog: HelpRequestDialog

    fun getFullDateWithStringByTimeZone() : String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))
        val currentLocalTime = cal.time
        val date: SimpleDateFormat = SimpleDateFormat("dd-MM-yyy HH:mm:ss")
        date.setTimeZone(TimeZone.getTimeZone("GMT+3"))
        val localTime: String = date.format(currentLocalTime)

        return localTime
    }

    fun showAddHelpRequestDialog(v: View, userData: User, hV: HomeViewModel){
        addHelpRequestDialog = AddHelpRequestDialog(v, userData, hV)
        addHelpRequestDialog.setCancelable(false)
        addHelpRequestDialog.show()
    }

    fun showHelpRequestDialog(v: View, targetData: User, helpRequestData: HelpRequest, userData: User){
        helpRequestDialog = HelpRequestDialog(v, targetData, helpRequestData, userData)
        helpRequestDialog.setCancelable(false)
        helpRequestDialog.show()
    }

    fun getWidth(v: View) : Float = v.resources.displayMetrics.widthPixels.toFloat()
}