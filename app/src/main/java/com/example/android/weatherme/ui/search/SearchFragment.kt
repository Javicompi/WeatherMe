package com.example.android.weatherme.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.weatherme.R
import com.example.android.weatherme.data.network.models.current.toEntity
import com.example.android.weatherme.databinding.FragmentSearchBinding
import com.example.android.weatherme.utils.Constants
import com.example.android.weatherme.utils.hideKeyboard
import com.example.android.weatherme.utils.isInternetAvailable
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        binding.searchButton.setOnClickListener {
            hideKeyboard()
            if (!isInternetAvailable(this.requireActivity())) {
                showSnackBar(R.string.no_internet_connection)
            } else {
                val searchText = binding.searchEdittext.text.toString()
                viewModel.searchByName(searchText)
            }
        }

        binding.searchFab.setOnClickListener {
            if (!isInternetAvailable(this.requireActivity())) {
                showSnackBar(R.string.no_internet_connection)
            } else if (!locationPermissionGranted()) {
                val permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(permissionArray, Constants.REQUEST_LOCATION_PERMISSION_CODE)
            } else {
                getLocation()
            }
        }

        viewModel.current.observe(viewLifecycleOwner, {
            val action = SearchFragmentDirections.actionNavigationSearchToResultSearchFragment(current = it.toEntity())
            findNavController().navigate(action)
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            showSnackBar(resourceId = it)
        })

        viewModel.showSnackBar.observe(viewLifecycleOwner, {
            showSnackBar(message = it)
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                stopLocationUpdates()
                if (locationResult != null && locationResult.locations.isNotEmpty()) {
                    viewModel.searchByLocation(locationResult.locations[0])
                } else {
                    showSnackBar(resourceId = R.string.location_not_found)
                }
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                showSnackBar(R.string.location_permission_denied)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            val locationResult = LocationServices
                    .getFusedLocationProviderClient(requireActivity())
                    .lastLocation
            var lastKnownLocation: Location
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    viewModel.searchByLocation(lastKnownLocation)
                } else {
                    startLocationUpdates()
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            showSnackBar(resourceId = R.string.location_error)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(requireActivity())
                .removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = Constants.LOCATION_REQUEST_INTERVAL
            fastestInterval = Constants.LOCATION_REQUEST_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }

    private fun showSnackBar(resourceId: Int? = 0, message: String? = "") {
        if (resourceId != null && resourceId > 0) {
            Snackbar.make(requireView(), resourceId, Snackbar.LENGTH_LONG).show()
        } else if (message != null && message.isNotEmpty()) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun locationPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
    }
}