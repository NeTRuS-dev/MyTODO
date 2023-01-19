package com.example.mytodo.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mytodo.core.FileSystemWrapper
import com.example.mytodo.core.NotesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val notesManager: NotesManager) : ViewModel() {
    fun getNoteById(id: Int) = notesManager.getNoteById(id)
}