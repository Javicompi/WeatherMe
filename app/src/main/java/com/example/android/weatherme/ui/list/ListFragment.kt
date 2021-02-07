package com.example.android.weatherme.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.weatherme.databinding.FragmentListBinding
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(activity, ListViewModelFactory(activity.application))
            .get(ListViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = CurrentAdapter(CurrentListener { current ->
            val action = ListFragmentDirections.actionNavigationListToNavigationCurrent(current.key)
            findNavController().navigate(action)
        })

        binding.currentRecycler.adapter = adapter

        viewModel.currentList.observe(viewLifecycleOwner, { currentList ->
            currentList.let {
                adapter.submitList(currentList)
            }
        })

        viewModel.showSnackBar.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(viewLifecycleOwner, {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        return binding.root
    }
}