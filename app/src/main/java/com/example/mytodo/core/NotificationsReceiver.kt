package com.example.mytodo.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.example.mytodo.R
import com.example.mytodo.core.extensions.goAsync
import com.example.mytodo.core.models.Note
import com.example.mytodo.presentation.ARG_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint(BroadcastReceiver::class)
class NotificationsReceiver : Hilt_NotificationsReceiver() {

    @Inject
    lateinit var notesManager: NotesManager

    @Inject
    lateinit var alarmService: AlarmService

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let {
            intent?.let {
                val noteId = intent.getIntExtra(ARG_ID, -1)
                if (noteId != -1) {
                    goAsync {
                        getNoteById(noteId)?.let { note ->
                            if (!note.is_done) {
                                val redirectIntent = alarmService.createPendingIntentToFragment(
                                    R.id.noteFragment,
                                    bundleOf(ARG_ID to noteId)
                                )
                                alarmService
                                    .showNotification(
                                        noteId,
                                        note.name,
                                        note.content,
                                        redirectIntent
                                    )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getNoteById(noteId: Int): Note? = withContext(Dispatchers.IO) {
        notesManager.getNoteById(noteId)
    }
}