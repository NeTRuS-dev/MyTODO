package com.example.mytodo.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mytodo.core.FileSystemWrapper
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val notesManager: NotesManager) : ViewModel() {
    suspend fun getNoteById(id: Int) = withContext(Dispatchers.IO) {
        notesManager.getNoteById(id)
    }

    suspend fun addNewNote(name: String, content: String) = withContext(Dispatchers.IO) {
        notesManager.addNewNote(
            Note(
                name = name,
                content = content,
                created_at = Date().time,
                updated_at = Date().time
            )
        )
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        notesManager.updateNote(
            note.apply {
                updated_at = Date().time
            }
        )
    }
}