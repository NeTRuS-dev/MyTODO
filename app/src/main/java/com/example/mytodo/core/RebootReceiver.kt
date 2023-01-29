package com.example.mytodo.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.mytodo.core.extensions.goAsync

@AndroidEntryPoint(BroadcastReceiver::class)
class RebootReceiver : Hilt_RebootReceiver() {
    @Inject
    lateinit var notesManager: NotesManager

    @Inject
    lateinit var alarmService: AlarmService
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let {
            intent?.let {
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    goAsync {
                        notesManager.getAllAlarmsOnce().forEach { alarm ->
                            notesManager.getNoteById(alarm.noteId)?.let { note ->
                                alarm.alarmCode?.let { alarmCode ->
                                    alarmService.createAlarm(
                                        note,
                                        alarm.notification_time,
                                        alarmCode
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
