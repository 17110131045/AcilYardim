package com.nexis.acilyardim.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBar
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.AddHelpRequestDialogBinding
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.HomeViewModel

class AddHelpRequestDialog(val v: View, val userData: User, val hV: HomeViewModel) : Dialog(v.context), View.OnClickListener {
    private lateinit var addHelpRequestBinding: AddHelpRequestDialogBinding
    private lateinit var txtRequestContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addHelpRequestBinding = AddHelpRequestDialogBinding.inflate(LayoutInflater.from(v.context))
        setContentView(addHelpRequestBinding.root)

        addHelpRequestBinding.user = userData

        window?.let {
            it.setLayout((AppUtil.getWidth(v) * 0.8f).toInt(), ActionBar.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }

        addHelpRequestBinding.addHelpRequestDialogImgClose.setOnClickListener(this)
        addHelpRequestBinding.addHelpRequestDialogBtnAddRequest.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.add_help_request_dialog_imgClose -> closeThisDialog()
                R.id.add_help_request_dialog_btnAddRequest -> addNewHelpRequest()
            }
        }
    }

    private fun closeThisDialog(){
        if (this.isShowing)
            this.dismiss()
    }

    private fun addNewHelpRequest(){
        txtRequestContent = addHelpRequestBinding.addHelpRequestDialogEditRequest.text.toString().trim()

        if (txtRequestContent.isEmpty()){
            "message".show(addHelpRequestBinding.root, "Lütfen ihtiyaç talebinizi giriniz")
            return
        }

        closeThisDialog()
        hV.addNewHelpRequest(userData.userId, txtRequestContent)
    }
}