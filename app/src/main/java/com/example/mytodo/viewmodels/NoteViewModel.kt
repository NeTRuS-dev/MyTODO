package com.example.mytodo.viewmodels

import androidx.lifecycle.*
import com.example.mytodo.core.AlarmService
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.core.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesManager: NotesManager,
    private val alarmService: AlarmService,
) : ViewModel() {

    val noteId: MutableLiveData<Int> = MutableLiveData()
    val note: LiveData<Note> = Transformations.switchMap(noteId) { id ->
        liveData {
            emit(notesManager.getNoteById(id))
        }
    }
    val noteAlarms: LiveData<List<Alarm>> = Transformations.switchMap(noteId) { id ->
        notesManager.getAlarmsByNoteId(id).asLiveData()
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
                    name = name, content = content, updated_at = Date().time
                )
            )
        }
    }

    suspend fun deleteCurrentNote() = withContext(Dispatchers.IO) {
        note.value?.let { note ->
            notesManager.deleteNoteAndAlarms(alarmService, note)
        }
    }

    suspend fun deleteAlarm(alarm: Alarm) = withContext(Dispatchers.IO) {
        alarm.alarmCode?.let { alarmCode ->
            alarmService.cancelAlarm(note.value!!, alarmCode)
        }
        notesManager.deleteAlarm(note.value, alarm)
    }

    suspend fun addAlarm(dateInMilliSecondsUtc: Long, hour: Int, minute: Int) =
        withContext(Dispatchers.IO) {
            note.value?.let {
                val notificationTime = utcToLocalTime(dateInMilliSecondsUtc, hour, minute)
                val alarmCode =
                    alarmService.createAlarm(
                        it,
                        notificationTime,
                    )

                notesManager.addAlarm(
                    Alarm(
                        noteId = it.id,
                        notification_time = notificationTime,
                        alarmCode = alarmCode,

                        created_at = Date().time,
                        updated_at = Date().time,
                    )
                )
            }
        }

    private fun utcToLocalTime(dateInMilliSecondsUtc: Long, hour: Int, minute: Int): Long {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = dateInMilliSecondsUtc
        calendar.add(Calendar.HOUR_OF_DAY, hour)
        calendar.add(Calendar.MINUTE, minute)
        val locale: Locale = Locale.getDefault()
        val df: DateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", locale)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.format(calendar.time)
        df.timeZone = TimeZone.getDefault()
        // parse date to get milliseconds in GMT
        return df.parse(date)!!.time
    }
}