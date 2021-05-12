package com.example.android.weatherme.ui.weather.daily

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.weatherme.R
import com.example.android.weatherme.data.database.entities.DailyEntity
import com.example.android.weatherme.databinding.ListItemDailyBinding

class DailyAdapter  : ListAdapter<DailyEntity, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    private var expandedPosition = RecyclerView.NO_POSITION
    private var prevExpandedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isExpanded = position == expandedPosition
        holder.binding.listItemDailyDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
        if (isExpanded) {
            prevExpandedPosition = position
            holder.binding.listItemDailyArrow.setImageResource(R.drawable.ic_arrow_up_down)
        } else {
            holder.binding.listItemDailyArrow.setImageResource(R.drawable.ic_arrow_down_up)
        }
        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            if (isExpanded) {
                holder.binding.listItemDailyArrow.setImageResource(R.drawable.ic_arrow_up_down)
            } else {
                holder.binding.listItemDailyArrow.setImageResource(R.drawable.ic_arrow_down_up)
            }
            notifyItemChanged(prevExpandedPosition)
            notifyItemChanged(expandedPosition)
            (holder.binding.listItemDailyArrow.drawable as AnimatedVectorDrawable).start()
        }
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: ListItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyEntity) {
            binding.daily = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemDailyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DailyDiffCallback : DiffUtil.ItemCallback<DailyEntity>() {
    override fun areItemsTheSame(oldItem: DailyEntity, newItem: DailyEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DailyEntity, newItem: DailyEntity): Boolean {
        return oldItem == newItem
    }
}