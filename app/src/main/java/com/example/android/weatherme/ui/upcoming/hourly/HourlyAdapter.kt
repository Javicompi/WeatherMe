package com.example.android.weatherme.ui.upcoming.hourly

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.weatherme.data.database.entities.perhour.HourlyEntity
import com.example.android.weatherme.databinding.ListItemHourlyBinding

class HourlyAdapter : ListAdapter<HourlyEntity, HourlyAdapter.ViewHolder>(HourlyDiffCallback()) {

    private var expandedPosition = -1
    private var prevExpandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isExpanded = position == expandedPosition
        holder.binding.listItemHourlyDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        if (isExpanded) prevExpandedPosition = position
        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(prevExpandedPosition)
            notifyItemChanged(expandedPosition)

        }
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: ListItemHourlyBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyEntity) {
            binding.hourly = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHourlyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class HourlyDiffCallback : DiffUtil.ItemCallback<HourlyEntity>() {

    override fun areItemsTheSame(oldItem: HourlyEntity, newItem: HourlyEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HourlyEntity, newItem: HourlyEntity): Boolean {
        return oldItem == newItem
    }
}