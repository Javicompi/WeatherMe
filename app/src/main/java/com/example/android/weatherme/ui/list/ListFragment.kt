package com.example.android.weatherme.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.weatherme.R

class ListFragment : Fragment() {

    private val listViewModel: ListViewModel by lazy {
        val acitivy = requireNotNull(this.activity)
        ViewModelProvider(acitivy, ListViewModelFactory(acitivy.application))
            .get(ListViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        listViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}