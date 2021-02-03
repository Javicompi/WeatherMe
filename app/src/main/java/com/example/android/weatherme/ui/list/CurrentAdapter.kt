package com.example.android.weatherme.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.weatherme.data.database.entities.current.CurrentEntity
import com.example.android.weatherme.databinding.ListItemCurrentBinding

class CurrentAdapter(val clickListener: CurrentListener) :
    ListAdapter<CurrentEntity, CurrentAdapter.ViewHolder>(CurrentDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemCurrentBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(item: CurrentEntity, clickListener: CurrentListener) {
                binding.current = item
                binding.clickListener = clickListener
                binding.executePendingBindings()
            }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCurrentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CurrentListener(val clickListener: (current: CurrentEntity) -> Unit) {
    fun onClick(current: CurrentEntity) = clickListener(current)
}

class CurrentDiffCallback : DiffUtil.ItemCallback<CurrentEntity>() {

    override fun areItemsTheSame(oldItem: CurrentEntity, newItem: CurrentEntity): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: CurrentEntity, newItem: CurrentEntity): Boolean {
        return oldItem == newItem
    }
}