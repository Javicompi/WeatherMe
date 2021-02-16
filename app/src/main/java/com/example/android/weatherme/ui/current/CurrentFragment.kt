package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.google.android.material.snackbar.Snackbar

class CurrentFragment : Fragment() {

    private val viewModel: CurrentViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, CurrentViewModelFactory(activity.application))
                .get(CurrentViewModel::class.java)
    }

    private val arguments: CurrentFragmentArgs by navArgs()

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

        viewModel.showSnackBar.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        arguments.let {
            if (it.selectedCurrent > 0) {
                viewModel.loadNewCurrent.value = it.selectedCurrent
            }
        }
    }
}