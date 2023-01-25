package com.example.mytodo.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.databinding.AlarmItemBinding
import java.text.SimpleDateFormat
import java.util.*

class AlarmsAdapter(
    private val onOptionRemove: (Alarm) -> Boolean
) : ListAdapter<Alarm, AlarmsAdapter.AlarmsViewHolder>(AlarmsAdapter.DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Alarm>() {
            override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmsViewHolder {
        val viewHolder = AlarmsViewHolder(
            AlarmItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onOptionRemove
        )
        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val alarm = getItem(position)
            viewHolder.showPopupMenu(it, alarm)
            true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: AlarmsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlarmsViewHolder(
        private val binding: AlarmItemBinding,
        private val deleteItemCallback: ((Alarm) -> Boolean)
    ) : RecyclerView.ViewHolder(binding.root) {
        private var alarm: Alarm? = null
        fun bind(alarm: Alarm) {
            this.alarm = alarm
            val date = Date(alarm.notification_time)
            val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
            val strDate: String = formatter.format(date)
            binding.alarmDescription.text = strDate
        }

        fun showPopupMenu(view: View, alarm: Alarm) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.alarm_item_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.alarm_delete_option -> {
                            deleteItemCallback(alarm)
                        }
                    }
                    true
                }
                show()
            }
        }
    }
}