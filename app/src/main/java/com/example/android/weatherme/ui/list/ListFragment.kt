package com.example.android.weatherme.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.weatherme.R
import com.example.android.weatherme.databinding.FragmentListBinding
import com.example.android.weatherme.ui.MainViewModel
import com.example.android.weatherme.ui.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

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
        val binding = FragmentListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = CurrentAdapter(CurrentListener { current ->
            viewModel.onCurrentClicked(current)
            Navigation.findNavController(requireView()).navigate(R.id.navigation_current)
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

        viewModel.showSnackBar.observe(this, Observer {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })

        viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })
    }
}