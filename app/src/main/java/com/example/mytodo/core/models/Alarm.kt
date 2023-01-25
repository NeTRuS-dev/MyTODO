package com.example.mytodo.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Alarm(
    var notification_time: Long,
    var noteId: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}