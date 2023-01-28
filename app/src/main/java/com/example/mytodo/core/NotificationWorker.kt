package com.example.mytodo.core

import android.content.Context
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mytodo.R
import com.example.mytodo.core.models.Note
import com.example.mytodo.presentation.ARG_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notesManager: NotesManager,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alarmService = AlarmService(applicationContext)
        val noteId = inputData.getInt(ARG_ID, -1)
        if (noteId == -1) {
            return Result.failure()
        }
        val note = getNoteById(noteId) ?: return Result.failure()

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
        return Result.success()
    }

    private suspend fun getNoteById(noteId: Int): Note? = withContext(Dispatchers.IO) {
        notesManager.getNoteById(noteId)
    }
}