package com.example.android.weatherme.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    @Inject
    lateinit var repository: Repository

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = CurrentAdapter(CurrentListener { current ->
            val action = ListFragmentDirections.actionNavigationListToNavigationCurrent(current.cityId)
            findNavController().navigate(action)
        })

        binding.currentRecycler.adapter = adapter

        viewModel.currentList.observe(viewLifecycleOwner, { currentList ->
            currentList.let {
                adapter.submitList(currentList)
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    override fun onStop() {
        super.onStop()

        viewLifecycleOwner.lifecycle.removeObserver(viewModel)
    }
}