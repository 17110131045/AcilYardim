package com.nexis.acilyardim.view

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.nexis.acilyardim.R
import com.nexis.acilyardim.databinding.FragmentHomeBinding
import com.nexis.acilyardim.model.HelpRequest
import com.nexis.acilyardim.model.User
import com.nexis.acilyardim.util.AppUtil
import com.nexis.acilyardim.util.FirebaseUtil
import com.nexis.acilyardim.util.show
import com.nexis.acilyardim.viewmodel.HomeViewModel

class HomeFragment(val userId: String) : Fragment(), OnMapReadyCallback {
    private lateinit var v: View
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean: Boolean = false
    private var lastUserLocation: LatLng? = null

    private lateinit var userData: User
    private lateinit var helpRequestList: ArrayList<HelpRequest>
    private lateinit var helpRequestUserList: ArrayList<User>
    private lateinit var userLatLng: LatLng
    private lateinit var selectUserFromMark: User
    private var selectUserIn: Int = 0

    private fun init(){
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        observeLiveData()
        homeViewModel.getUserData(userId)

        val mapFragment = childFragmentManager.findFragmentById(R.id.home_fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()
        sharedPreferences = v.context.getSharedPreferences("com.nexis.acilyardim", AppCompatActivity.MODE_PRIVATE)
        homeBinding.homeFragmentImgAddNewHelpRequest.isClickable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun observeLiveData(){
        homeViewModel.userData.observe(viewLifecycleOwner, Observer {
            it?.let {
                userData = it
                homeViewModel.getHelpRequests()
                homeBinding.homeFragmentImgAddNewHelpRequest.isClickable = true
                homeBinding.homeFragmentImgAddNewHelpRequest.setOnClickListener { AppUtil.showAddHelpRequestDialog(v, userData, homeViewModel) }
            }
        })

        homeViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        homeViewModel.successMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.show(v, it)
            }
        })

        homeViewModel.helpRequestList.observe(viewLifecycleOwner, Observer {
            it?.let {
                helpRequestList = it
                setUpUserLocations(helpRequestList, mMap)
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationManager = v.context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)

                if (!trackBoolean){
                    val userLocation: LatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale((v.context as Activity), Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(homeBinding.root, "Konum izni gerekiyor", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
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

    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result){
                if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                    setUpLastLocationFromMap()
                }
            } else {
                Toast.makeText(v.context, "İznin verilmesi gerekiyor", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpLastLocationFromMap(){
        if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val lastLocation: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            lastLocation?.let {
                lastUserLocation = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation!!, 15f))
            }

            mMap.isMyLocationEnabled = true
        }
    }

    private fun setUpUserLocations(helpRequestList: ArrayList<HelpRequest>, map: GoogleMap){
        helpRequestUserList = ArrayList()

        for (hR in helpRequestList.indices){
            FirebaseUtil.getUserData(helpRequestList[hR].helpRequestUserId){ userData ->
                userData?.let {
                    helpRequestUserList.add(it)
                    userLatLng = LatLng(it.userLatitude, it.userLongitude)
                    map.addMarker(MarkerOptions().position(userLatLng).title(it.userName))

                    if (hR == (helpRequestList.size - 1)){
                        map.setOnMarkerClickListener {
                            selectUserIn = getUserDataFromMarkers(it.position, helpRequestUserList)
                            selectUserFromMark = helpRequestUserList[selectUserIn]
                            AppUtil.showHelpRequestDialog(v, selectUserFromMark, helpRequestList[selectUserIn], this.userData)
                            return@setOnMarkerClickListener false
                        }
                    }
                }
            }
        }
    }

    private fun getUserDataFromMarkers(markLocation: LatLng, userList: ArrayList<User>) : Int {
        var uIn: Int = 0

        for (u in userList.indices){
            if (userList[u].userLatitude == markLocation.latitude && userList[u].userLongitude == markLocation.longitude)
                return u
        }

        return uIn
    }
}