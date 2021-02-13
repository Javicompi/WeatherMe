package com.example.android.weatherme.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.databinding.FragmentResultSearchBinding
import com.google.android.material.snackbar.Snackbar

class ResultSearchFragment : Fragment() {

    private val TAG = ResultSearchFragment::class.java.simpleName

    private val arguments: ResultSearchFragmentArgs by navArgs()

    private val viewModel: ResultSearchViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, ResultSearchViewModelFactory(activity.application))
                .get(ResultSearchViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentResultSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setCurrent(arguments.current)

        binding.searchedCurrent.currentFab.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_input_add, context?.theme)
        )
        binding.searchedCurrent.currentFab.setOnClickListener {
            viewModel.saveCurrent()
        }
        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })
        viewModel.navigateToCurrentFragment.observe(viewLifecycleOwner, { id ->
            val action = ResultSearchFragmentDirections.actionResultSearchFragmentToNavigationCurrent(id)
            findNavController().navigate(action)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "back button clicked")
                findNavController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}