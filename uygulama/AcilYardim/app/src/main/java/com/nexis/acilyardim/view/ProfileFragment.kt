package com.nexis.acilyardim.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.FragmentProfileBinding
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.ProfileViewModel

class ProfileFragment(val userId: String) : Fragment() {
    private lateinit var v: View
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var userData: User

    private fun init(){
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        observeLiveData()
        profileViewModel.getUserData(userId)

        profileBinding.profileFragmentBtnSignOut.setOnClickListener { signOut() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        profileViewModel.userData.observe(viewLifecycleOwner, Observer {
            it?.let {
                userData = it
                profileBinding.user = it
            }
        })
    }

    private fun signOut(){
        "message".show(v, "Oturumunuz kapatalıyor...")
        profileBinding.profileFragmentBtnSignOut.isClickable = false

        Handler(Looper.myLooper()!!).postDelayed({
            FirebaseUtil.mAuth.signOut()
            Navigation.findNavController(v).popBackStack()
        }, 1500)
    }
}