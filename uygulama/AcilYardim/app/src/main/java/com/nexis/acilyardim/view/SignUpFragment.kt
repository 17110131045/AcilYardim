package com.nexis.acilyardim.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.FragmentSignUpBinding
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.SignUpViewModel

class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var signUpBinding: FragmentSignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    private lateinit var txtUserName: String
    private lateinit var txtUserEmail: String
    private lateinit var txtUserPassword: String

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var lastUserLocation: LatLng? = null
    private var locationIsGranted: Boolean = true

    private fun init(){
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        observeLiveData()

        locationManager = v.context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener {}
        registerLauncher()

        signUpBinding.signUpFragmentBtnSignUp.setOnClickListener(this)
        signUpBinding.signUpFragmentBtnSignIn.setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signUpBinding = FragmentSignUpBinding.inflate(inflater, container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()

        if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale((v.context as Activity), Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(signUpBinding.root, "Konum izni gerekiyor", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            setUpLastLocationFromMap()
        }
    }

    private fun observeLiveData(){
        signUpViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
                signUpBinding.signUpFragmentBtnSignUp.isClickable = true
            }
        })

        signUpViewModel.successMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
                signUpBinding.signUpFragmentBtnSignUp.isClickable = true
                clearAllEdits()
                backToPage()
            }
        })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.sign_up_fragment_btnSignUp -> signUp()
                R.id.sign_up_fragment_btnSignIn -> backToPage()
            }
        }
    }

    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result){
                if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                    setUpLastLocationFromMap()
                }
            } else {
                locationIsGranted = false
                Toast.makeText(v.context, "İznin verilmesi gerekiyor", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpLastLocationFromMap(){
        if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val lastLocation: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            lastLocation?.let {
                lastUserLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    private fun signUp(){
        txtUserName = signUpBinding.signUpFragmentEditUserName.text.toString().trim()
        txtUserEmail = signUpBinding.signUpFragmentEditEmail.text.toString().trim()
        txtUserPassword = signUpBinding.signUpFragmentEditPassword.text.toString().trim()

        if (txtUserName.isEmpty()){
            "message".show(v, "Lütfen geçerli bir kullanıcı adı belirleyiniz")
            return
        }
        if (txtUserEmail.isEmpty()){
            "message".show(v, "Lütfen geçerli bir email adresi giriniz")
            return
        }
        if (txtUserPassword.isEmpty()){
            "message".show(v, "Lütfen geçerli bir şifre belirleyiniz")
            return
        }
        if (!locationIsGranted){
            "message".show(v, "Konum iznini vermeniz gerekiyor")
            return
        }
        if (lastUserLocation == null){
            "message".show(v, "Konum bilgisi alınamadı")
            return
        }

        signUpBinding.signUpFragmentBtnSignUp.isClickable = false
        signUpViewModel.signUpUser(txtUserEmail, txtUserPassword, txtUserName, lastUserLocation!!)
    }

    private fun clearAllEdits(){
        signUpBinding.signUpFragmentEditUserName.setText("")
        signUpBinding.signUpFragmentEditEmail.setText("")
        signUpBinding.signUpFragmentEditPassword.setText("")
    }

    private fun backToPage(){
        Navigation.findNavController(v).popBackStack()
    }
}