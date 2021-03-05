package com.example.android.weatherme.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentResultSearchBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultSearchFragment : Fragment() {

    private val arguments: ResultSearchFragmentArgs by navArgs()

    private val viewModel: ResultSearchViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentResultSearchBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setCurrent(arguments.current)
        viewModel.searchPerHour(arguments.current)

        binding.searchedCurrent.currentFab.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_input_add, context?.theme)
        )

        binding.searchedCurrent.currentFab.setOnClickListener {
            viewModel.saveData()
        }

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            showSnackBar(resourceId = it)
        })

        viewModel.showSnackBarMessage.observe(viewLifecycleOwner, {
            showSnackBar(message = it)
        })

        viewModel.navigateToCurrentFragment.observe(viewLifecycleOwner, { id ->
            val action = ResultSearchFragmentDirections.actionResultSearchFragmentToNavigationCurrent(id)
            findNavController().navigate(action)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun showSnackBar(resourceId: Int? = 0, message: String? = "") {
        if (resourceId != null && resourceId > 0) {
            Snackbar.make(requireView(), resourceId, Snackbar.LENGTH_LONG).show()
        } else if (message != null && message.isNotEmpty()) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}