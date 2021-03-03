package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentFragment : Fragment() {

    private val viewModel: CurrentViewModel by viewModels()

    private val arguments: CurrentFragmentArgs by navArgs()

    private var currentSelected: Long = 0L

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.loadedCurrent.currentFab.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear, context?.theme)
        )
        binding.loadedCurrent.currentFab.setOnClickListener {
            viewModel.deleteCurrent()
        }

        viewModel.showSnackBar.observe(viewLifecycleOwner, { message ->
            Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, { value ->
            Snackbar.make(this.requireView(), getString(value), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (arguments.selectedCurrent > 0) {
            viewModel.loadNewCurrent.postValue(arguments.selectedCurrent)
        }
    }
}