package com.application.weatherforecast.presentation.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.application.weatherforecast.R
import com.application.weatherforecast.model.CurrentCityForecast
import com.application.weatherforecast.presentation.ui.activities.MainActivity
import com.application.weatherforecast.presentation.ui.adapters.FiveDaysAdapter
import com.application.weatherforecast.presentation.viewmodel.CurrentCityViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_current_city_forecast.*


class CurrentCityForecastFragment : Fragment() {

    private var latitude = ""
    private var longitude = ""

    lateinit var viewModel: CurrentCityViewModel
    lateinit var fiveDaysAdapter: FiveDaysAdapter

    private val PERMISSION_ID = 805
    private val CHECK_SETTINGS_REQUEST = 0x1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var updateLocation = true
    private lateinit var activity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity = getActivity()!!
        return inflater.inflate(R.layout.fragment_current_city_forecast, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        settingsClient = LocationServices.getSettingsClient(activity)

        viewModel = ViewModelProviders.of(this).get(CurrentCityViewModel::class.java)
        observeViewModel()
        setupView()

        initializeLocationCallback()
        initializeLocationRequest()
        buildRequest()

        getLocation()
    }

    private fun setupView() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loading.value = true
            getLocation()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestLocationPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (shouldProvideRationale) {
            // Request permission
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_ID
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_ID
            )
        }
    }

    fun requestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (activity is MainActivity) {
                    findNavController().popBackStack(R.id.home_dest, false)
                    (activity as MainActivity).showSnackbar(
                        R.string.permission_denied_explanation,
                        R.string.settings
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        requestPermissionsResult(requestCode, grantResults)
    }


    private fun getLocation() {
        if (checkLocationPermissions()) {
            updateLocations()
        } else {
            requestLocationPermissions()
        }
    }

    private fun initializeLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val mLastLocation: Location = locationResult.lastLocation
                latitude = mLastLocation.latitude.toString()
                longitude = mLastLocation.longitude.toString()
                updateLocation = false
                stopLocationUpdates()
                viewModel.fetchForecast(latitude, longitude)
            }
        }
    }

    private fun initializeLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    private fun buildRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun updateLocations() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(activity) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )
            }
            .addOnFailureListener(activity) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                activity,
                                CHECK_SETTINGS_REQUEST
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        updateLocation = false
                    }
                }
            }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.let {
            it.removeLocationUpdates(locationCallback)?.let {
                it.addOnCompleteListener(activity) {
                    updateLocation = false
                }
            }
        }
    }

    fun activityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            CHECK_SETTINGS_REQUEST -> when (resultCode) {
                Activity.RESULT_OK -> {
                    updateLocations()
                }
                Activity.RESULT_CANCELED -> {
                    updateLocation = false
                    findNavController().popBackStack(R.id.home_dest, false)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.cityName.observe(this, Observer { name ->
            name?.let {
                tvTitle.text = name
            }
        })
        viewModel.forecast.observe(this, Observer { forecast ->
            forecast?.let {
                rlForecast.visibility = View.VISIBLE
                rvFiveDays.visibility = View.VISIBLE
                setupForecastView(forecast as ArrayList<CurrentCityForecast>)
            }
        })
        viewModel.forecastLoadError.observe(this, Observer { isError ->
            isError?.let {
                rlForecast.visibility = View.VISIBLE
                listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                swipeRefresh.isRefreshing = it
                if (it) {
                    rlForecast.visibility = View.VISIBLE
                    rvFiveDays.visibility = View.GONE
                }
            }
        })
    }

    private fun setupForecastView(forecast: ArrayList<CurrentCityForecast>) {
        fiveDaysAdapter = FiveDaysAdapter(forecast)
        rvFiveDays.adapter = fiveDaysAdapter
    }

}