package com.nexis.acilyardim.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.FragmentSignInBinding
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.SignInViewModel

class SignInFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var signInBinding: FragmentSignInBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var navDirections: NavDirections

    private lateinit var txtUserEmail: String
    private lateinit var txtUserPassword: String

    private fun init(){
        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        observeLiveData()
        signInViewModel.checkSigned()

        signInBinding.signInFragmentBtnSignIn.setOnClickListener(this)
        signInBinding.signInFragmentBtnSignUp.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInBinding = FragmentSignInBinding.inflate(inflater, container, false)
        return signInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        signInViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
                signInBinding.signInFragmentBtnSignIn.isClickable = true
            }
        })

        signInViewModel.successMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
                signInBinding.signInFragmentBtnSignIn.isClickable = true
            }
        })

        signInViewModel.signedUserId.observe(viewLifecycleOwner, Observer {
            it?.let {
                goToMainPage(it)
            }
        })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.sign_in_fragment_btnSignIn -> signInUser()
                R.id.sign_in_fragment_btnSignUp -> goToSignUpPage()
            }
        }
    }

    private fun signInUser(){
        txtUserEmail = signInBinding.signInFragmentEditEmail.text.toString().trim()
        txtUserPassword = signInBinding.signInFragmentEditPassword.text.toString().trim()

        if (txtUserEmail.isEmpty()){
            "message".show(v, "Lütfen email adresinizi giriniz")
            return
        }
        if (txtUserPassword.isEmpty()){
            "message".show(v, "Lütfen şifrenizi giriniz")
            return
        }

        signInBinding.signInFragmentBtnSignIn.isClickable = false
        signInViewModel.signInUser(txtUserEmail, txtUserPassword)
    }

    private fun goToSignUpPage(){
        navDirections = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        Navigation.findNavController(v).navigate(navDirections)
    }

    private fun goToMainPage(userId: String){
        navDirections = SignInFragmentDirections.actionSignInFragmentToMainFragment(userId)
        Navigation.findNavController(v).navigate(navDirections)
    }
}