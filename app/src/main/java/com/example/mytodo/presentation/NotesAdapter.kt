package com.example.mytodo.presentation

import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.databinding.NoteItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private val onItemClicked: (Note) -> Unit,
    private val onOptionEdit: (Note) -> Boolean,
    private val onOptionRemove: (Note) -> Boolean
) : ListAdapter<Note, NotesAdapter.NotesViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val viewHolder = NotesViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onOptionEdit,
            onOptionRemove
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val note = getItem(position)
            viewHolder.showPopupMenu(it, note)
            true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotesViewHolder(
        private val binding: NoteItemBinding,
        private val editItemCallback: ((Note) -> Boolean),
        private val deleteItemCallback: ((Note) -> Boolean)
    ) : RecyclerView.ViewHolder(binding.root) {
        private var note: Note? = null
        fun bind(note: Note) {
            this.note = note
            binding.noteTitle.text = note.name

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val netDate = Date(note.updated_at)
            binding.noteUpdatedAt.text = sdf.format(netDate)
        }

        fun showPopupMenu(view: View, note: Note) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.note_item_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_option -> {
                            editItemCallback(note)
                        }
                        R.id.delete_option -> {
                            deleteItemCallback(note)
                        }
                    }
                    true
                }
                show()
            }
        }
    }
}
