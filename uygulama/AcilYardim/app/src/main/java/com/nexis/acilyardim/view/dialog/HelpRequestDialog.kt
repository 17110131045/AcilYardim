package com.nexis.acilyardim.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.HelpRequestDialogBinding
import com.nexis.acilyardim.model.HelpRequest
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.view.MainFragmentDirections

class HelpRequestDialog(val v: View, val targetData: User, val helpRequestData: HelpRequest, val userData: User) : Dialog(v.context), View.OnClickListener {
    private lateinit var helpRequestBinding: HelpRequestDialogBinding
    private lateinit var navDirections: NavDirections

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helpRequestBinding = HelpRequestDialogBinding.inflate(LayoutInflater.from(v.context))
        setContentView(helpRequestBinding.root)

        helpRequestBinding.user = targetData
        helpRequestBinding.helprequest = helpRequestData

        if (targetData.userId == userData.userId)
            helpRequestBinding.helpRequestDialogBtnSendMessage.text = "Kapat"

        window?.let {
            it.setLayout((AppUtil.getWidth(v) * 0.8f).toInt(), ActionBar.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }

        helpRequestBinding.helpRequestDialogImgClose.setOnClickListener(this)
        helpRequestBinding.helpRequestDialogBtnSendMessage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.help_request_dialog_imgClose -> closeThisDialog()
                R.id.help_request_dialog_btnSendMessage -> goToChatPage()
            }
        }
    }

    private fun closeThisDialog(){
        if (this.isShowing)
            this.dismiss()
    }

    private fun goToChatPage(){
        closeThisDialog()

        if (helpRequestBinding.helpRequestDialogBtnSendMessage.text != "Kapat") {
            navDirections = MainFragmentDirections.actionMainFragmentToChatFragment(userData, targetData)
            Navigation.findNavController(v).navigate(navDirections)
        }
    }
}