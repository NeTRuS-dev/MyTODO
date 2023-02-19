package com.example.mytodo.presentation

import android.graphics.Paint
import android.view.*
import androidx.appcompat.widget.PopupMenu
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
    private val onOptionMarkAsDone: (Note) -> Unit,
    private val onOptionMarkAsNotDone: (Note) -> Unit,
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
            onItemClicked,
            onOptionMarkAsDone,
            onOptionMarkAsNotDone,
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
        private val editItemCallback: ((Note) -> Unit),
        private val markAsDoneItemCallback: ((Note) -> Unit),
        private val markAsUndoneItemCallback: ((Note) -> Unit),
        private val deleteItemCallback: ((Note) -> Boolean),
    ) : RecyclerView.ViewHolder(binding.root) {
        private var note: Note? = null
        fun bind(note: Note) {
            this.note = note
            binding.noteTitle.text = note.name

            // get first 100 symbols from note content and add ... if content length > 100
            if (note.content != null) {
                val content = note.content!!
                binding.noteContentPreview.text = if (content.length > 100) {
                    content.substring(0, 100) + "..."
                } else {
                    content
                }
            }

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val netDate = Date(note.updated_at)
            binding.noteUpdatedAt.text = sdf.format(netDate)
            if (note.is_done) {
                binding.noteTitle.paintFlags =
                    binding.noteTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.noteContentPreview.paintFlags =
                    binding.noteContentPreview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        fun showPopupMenu(view: View, note: Note) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.note_item_menu)
                if (note.is_done) {
                    menu.findItem(R.id.mark_as_done).isVisible = false
                } else {
                    menu.findItem(R.id.mark_as_undone).isVisible = false
                }
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_option -> {
                            editItemCallback(note)
                        }
                        R.id.delete_option -> {
                            deleteItemCallback(note)
                        }
                        R.id.mark_as_done -> {
                            markAsDoneItemCallback(note)
                        }
                        R.id.mark_as_undone -> {
                            markAsUndoneItemCallback(note)
                        }
                    }
                    true
                }
                show()
            }
        }
    }
}
