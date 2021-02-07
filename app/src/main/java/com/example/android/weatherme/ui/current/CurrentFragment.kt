package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.google.android.material.snackbar.Snackbar

class CurrentFragment : Fragment() {

    private val TAG = CurrentFragment::class.java.simpleName

    /*private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, MainViewModelFactory(activity.application))
                .get(MainViewModel::class.java)
    }*/

    private val viewModel: CurrentViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, CurrentViewModelFactory(activity.application))
                .get(CurrentViewModel::class.java)
    }

    val arguments: CurrentFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.showSnackBar.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        viewModel.currentSelected.observe(viewLifecycleOwner, {
            Log.d(TAG, "currentSelected: ${it?.cityName}")
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.let {
            if (it.searchName.length >= 2) {
                Log.d(TAG, "searchByName")
                viewModel.searchCurrentByName(it.searchName)
            }
            if (it.searchLocation != null) {
                Log.d(TAG, "searchByLocation")
                viewModel.searchCurrentByLocation(it.searchLocation)
            }
            if (it.selectedCurrent > 0) {
                Log.d(TAG, "loadCurrent")
                viewModel.loadCurrent(it.selectedCurrent)
            }
        }
    }
}