package com.example.mytodo.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mytodo.core.FileSystemWrapper
import com.example.mytodo.core.NotesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val notesManager: NotesManager) : ViewModel() {
    suspend fun getAllNotes() = withContext(Dispatchers.IO) {
        notesManager.getAllNotes()
    }
}