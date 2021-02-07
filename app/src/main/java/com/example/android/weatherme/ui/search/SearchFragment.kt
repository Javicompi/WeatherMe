package com.example.android.weatherme.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentSearchBinding
import com.example.android.weatherme.ui.MainViewModel
import com.example.android.weatherme.ui.MainViewModelFactory
import com.example.android.weatherme.utils.Constants
import com.example.android.weatherme.utils.hideKeyboard
import com.example.android.weatherme.utils.isInternetAvailable
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {

    private val TAG = SearchFragment::class.java.simpleName

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, MainViewModelFactory(activity.application))
                .get(MainViewModel::class.java)
    }

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.searchButton.setOnClickListener {
            hideKeyboard()
            val searchText = binding.searchEdittext.text.toString()
            searchByName(name = searchText)
        }

        binding.searchFab.setOnClickListener {
            if (locationPermissionGranted()) {
                Log.d(TAG, "locationPermissionGranted")
                getLocation()
            } else {
                Log.d(TAG, "locationPermission not granted, requestionPermissions")
                val permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(permissionArray, Constants.REQUEST_LOCATION_PERMISSION_CODE)
            }
        }

        return binding.root
    }

    /*override fun onStart() {
        super.onStart()

        viewModel.showSnackBar.observe(this, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(this, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })
    }*/

    /*private fun searchCurrent(name: String = "", lat: Double = 0.0, lon: Double = 0.0) {
        if (isInternetAvailable(requireContext())) {
            if (name.isNotEmpty()) {
                viewModel.searchCurrent(name = name, lat, lon)
            } else {
                viewModel.searchCurrent(name, lat, lon)
            }
            Navigation.findNavController(requireView()).navigate(R.id.navigation_current)
        } else {
            viewModel.showSnackBarInt.postValue(R.string.no_internet_connection)
        }
    }*/

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult")
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission granted")
                getLocation()
            } else {
                Log.d(TAG, "permissions denied")
                showSnackBar(R.string.location_permission_denied)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Log.d(TAG, "getLocation")
        try {
            val locationResult = LocationServices
                    .getFusedLocationProviderClient(requireActivity())
                    .lastLocation
            var lastKnownLocation: Location
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    Log.d(TAG, "last Location found: ${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}")
                    searchByLocation(lastKnownLocation)
                } else {
                    Log.d(TAG, "last Location is null")
                    showSnackBar(R.string.location_not_found)
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            showSnackBar(R.string.location_error)
        }
    }

    private fun searchByName(name: String) {
        val action = SearchFragmentDirections.actionNavigationSearchToNavigationCurrent(searchName = name)
        findNavController().navigate(action)
    }

    private fun searchByLocation(location: Location) {
        val action = SearchFragmentDirections.actionNavigationSearchToNavigationCurrent(searchLocation = location)
        findNavController().navigate(action)
    }

    private fun showSnackBar(resourceId: Int) {
        Snackbar.make(requireView(), resourceId, Snackbar.LENGTH_LONG).show()
    }

    private fun locationPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
    }
}