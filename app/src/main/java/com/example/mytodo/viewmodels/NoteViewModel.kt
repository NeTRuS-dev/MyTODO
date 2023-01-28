package com.example.mytodo.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.lifecycle.*
import com.example.mytodo.core.NotesManager
import com.example.mytodo.core.NotificationsReceiver
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.core.models.Note
import com.example.mytodo.presentation.ARG_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesManager: NotesManager,
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

    suspend fun deleteAlarm(context: Context, alarm: Alarm) = withContext(Dispatchers.IO) {
        alarm.alarmCode?.let { alarmCode ->
            cancelAlarm(context, alarmCode)
        }
        notesManager.deleteAlarm(note.value, alarm)
    }

    suspend fun addAlarm(context: Context, dateInMilliSeconds: Long, hour: Int, minute: Int) =
        withContext(Dispatchers.IO) {
            note.value?.let {
                val notificationTime =
                    dateInMilliSeconds + hour * 60 * 60 * 1000 + minute * 60 * 1000

                val alarmCode = createAlarm(context, notificationTime)

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

    private fun cancelAlarm(context: Context, alarmCode: Int) {
        note.value?.let {
            val pendingIntent = buildAlarmIntent(context, alarmCode, it.id)

            val alarmManager = context.getSystemService(ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
        }
    }

    private fun createAlarm(context: Context, executeAt: Long): Int? {
        note.value?.let {
            val requestCode = Random().nextInt()
            val pendingIntent = buildAlarmIntent(context, requestCode, it.id)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as? AlarmManager
            alarmManager?.set(AlarmManager.RTC_WAKEUP, executeAt, pendingIntent)

            return@createAlarm requestCode
        }

        return null
    }

    private fun buildAlarmIntent(context: Context, alarmCode: Int, noteId: Int): PendingIntent {
        val intent = Intent(context, NotificationsReceiver::class.java)
        intent.putExtra(ARG_ID, noteId)
        return PendingIntent.getBroadcast(
            context,
            alarmCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}