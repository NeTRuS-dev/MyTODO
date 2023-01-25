package com.example.mytodo.viewmodels

import androidx.lifecycle.*
import com.example.mytodo.core.FileSystemWrapper
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.core.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val notesManager: NotesManager) : ViewModel() {
    val noteId: MutableLiveData<Int> = MutableLiveData()
    val note: LiveData<Note> = Transformations.switchMap(noteId) { id ->
        liveData {
            emit(notesManager.getNoteById(id))
        }
    }
    val noteAlarms: LiveData<List<Alarm>> = Transformations.switchMap(noteId) { id ->
        notesManager
            .getAlarmsByNoteId(id)
            .asLiveData()
    }

    suspend fun writeNote(name: String, content: String) = withContext(Dispatchers.IO) {
        if (note.value == null) {
            notesManager.addNewNote(
                Note(
                    name = name,
                    content = content,
                    created_at = Date().time,
                    updated_at = Date().time
                )
            )
        } else {
            notesManager.updateNote(
                note.value!!.copy(
                    name = name,
                    content = content,
                    updated_at = Date().time
                )
            )
        }
    }

    suspend fun deleteAlarm(alarm: Alarm) = withContext(Dispatchers.IO) {
        notesManager.deleteAlarm(note.value, alarm)
    }

    suspend fun addAlarm(dateInMilliSeconds: Long, hour: Int, minute: Int) =
        withContext(Dispatchers.IO) {
            notesManager.addAlarm(
                Alarm(
                    noteId = note.value!!.id,
                    notification_time = dateInMilliSeconds + hour * 60 * 60 * 1000 + minute * 60 * 1000,
                )
            )
        }
}