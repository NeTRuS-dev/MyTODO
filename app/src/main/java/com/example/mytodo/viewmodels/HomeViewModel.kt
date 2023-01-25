package com.example.mytodo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.mytodo.core.FileSystemWrapper
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val notesManager: NotesManager) : ViewModel() {
    val allNotes: LiveData<List<Note>> = liveData {
        getAllNotes().collect {
            emit(it)
        }
    }

    suspend fun getAllNotes() = withContext(Dispatchers.IO) {
        notesManager.getAllNotes()
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        notesManager.deleteNote(note)
    }
}