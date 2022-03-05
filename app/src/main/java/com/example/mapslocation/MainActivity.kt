package com.example.mapslocation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mapslocation.data.network.ApiInterface
import com.example.mapslocation.data.network.Network
import com.example.mapslocation.data.network.NetworkHandler
import com.example.mapslocation.databinding.ActivityMainBinding
import com.example.mapslocation.domain.DirectionsRepositoryImpl
import com.example.mapslocation.domain.FetchDirectionsUseCase
import com.example.mapslocation.domain.base.State
import com.example.mapslocation.fragments.BottomSheetDestinationDetails
import com.example.mapslocation.fragments.SearchAutoCompleteDialog
import com.example.mapslocation.utils.ItemPlace
import com.example.mapslocation.utils.ext.checkPermission
import com.example.mapslocation.utils.ext.isPermissionGranted
import com.example.mapslocation.utils.ext.requestOnePermission
import com.example.mapslocation.utils.ext.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places


const val REQUEST_CODE_LOCATION = 617254

class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap
    private var origin: LatLng? = null
    private var destinationLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentPolyline: Polyline? = null
    private var mDestinationMarker: Marker? = null
    private var mDestinationItemPlace: ItemPlace? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initObservers()
        initToolbar()

        // validate permission for location or request permission
        if (checkPermission(ACCESS_FINE_LOCATION)) {
            initFusedLocation()
            initMap()
        } else
            requestOnePermission(ACCESS_FINE_LOCATION, REQUEST_CODE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun initFusedLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                    updateMapLocation(location)
            }
    }

    private fun updateMapLocation(location: Location?) {
        origin = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
            location?.latitude ?: 0.0,
            location?.longitude ?: 0.0)))

        mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
    }

    private fun initObservers() {
        viewModel.routesState.observe(this, this::onRouteStateChange)
    }

    private fun onRouteStateChange(result: State<List<LatLng>>?) {
        result?.let { noNullResult ->
            when (noNullResult) {
                is State.Failure -> {
                    Log.e("Main","onRouteStateChange",noNullResult.exception)
                }
                is State.Progress -> {}
                is State.Success -> {
                    addPolylineMap(noNullResult.data)
                }
            }
        }
    }

    private fun addPolylineMap(points: List<LatLng>) {
        val options = PolylineOptions().apply {
            width(6f)
            startCap(ButtCap())
            jointType(JointType.ROUND)
            color(ContextCompat.getColor(this@MainActivity, R.color.purple_700))
            addAll(points)
        }
        // before draw line/destination, clear map
        currentPolyline?.remove()

        // show line and add destination mark
        val latLngBuilder = LatLngBounds.builder()
        if (origin!=null && destinationLatLng!=null){
            latLngBuilder.include(origin!!)
            latLngBuilder.include(destinationLatLng!!)

            // add destination mark
            val markerOptions = MarkerOptions()
            markerOptions.position(destinationLatLng!!)
            mMap.clear()
            mDestinationMarker =mMap.addMarker(markerOptions)
        }

        // add line and animation zoom
        currentPolyline = mMap.addPolyline(options)
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(),100))
    }

    private fun initViewModel() {
        val api = Network.createNetworkClient(true).create(ApiInterface::class.java)
        val repository = DirectionsRepositoryImpl(api, NetworkHandler(this))
        val useCase = FetchDirectionsUseCase(repository)
        viewModel = MainViewModel(useCase)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.appSearchBar)
            initAutoComplete()
        return super.onOptionsItemSelected(item)
    }

    private fun initAutoComplete() {
        val dialog = SearchAutoCompleteDialog.newInstance(null)
        dialog.onSelectedItem { destinationItem, isLoading ->
            if (origin!=null && destinationItem!= null && destinationItem.hasLatLng()){
                mDestinationItemPlace = destinationItem
                destinationLatLng = destinationItem.getLatLng()
                viewModel.fetchDirections(
                    origin = "${origin!!.latitude},${origin!!.longitude}",
                    destination = "${destinationItem.latitude},${destinationItem.longitude}"
                )
            }
            //if (isLoading)
        }
        dialog.show(supportFragmentManager, "placesAutoComplete")
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.MAPS_API_KEY)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMyLocationClick(location: Location) {
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker == mDestinationMarker && mDestinationItemPlace!=null){
            val dialog = BottomSheetDestinationDetails(mDestinationItemPlace!!)
            dialog.isCancelable = true
            dialog.show(supportFragmentManager,"BottomSheetDestinationDetails")

            return false
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_LOCATION) {
            return
        }
        if (isPermissionGranted(permissions, grantResults, ACCESS_FINE_LOCATION)) {
            initFusedLocation()
            initMap()
        } else {
            toast("Permission was denied")
        }
    }
}