package com.example.android.weatherme.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentCurrentBinding
import com.example.android.weatherme.databinding.FragmentListBinding
import com.example.android.weatherme.ui.MainViewModel
import com.example.android.weatherme.ui.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CurrentFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, MainViewModelFactory(activity.application))
                .get(MainViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCurrentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.showSnackBar.observe(this, Observer {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })
    }
}