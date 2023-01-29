package com.example.mytodo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.mytodo.core.AlarmService
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesManager: NotesManager,
    private val alarmService: AlarmService
) : ViewModel() {
    val allNotes: LiveData<List<Note>> = liveData {
        getAllNotes().collect {
            emit(it)
        }
    }

    suspend fun getAllNotes() = withContext(Dispatchers.IO) {
        notesManager.getAllNotes()
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        val alarms = notesManager.getAlarmsForNoteIdOnce(note.id)
        alarms.forEach { alarm ->
            alarm.alarmCode?.let { alarmCode ->
                alarmService.cancelAlarm(note, alarmCode)
            }
            notesManager.deleteAlarm(note, alarm)
        }
        notesManager.deleteNote(note)
    }
}