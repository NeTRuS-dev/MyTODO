package com.example.mytodo.core.models

import androidx.room.*
import com.example.mytodo.core.UUIDTypeConverter

@Entity
@TypeConverters(UUIDTypeConverter::class)
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var notification_time: Long,
    var noteId: Int,
    var alarmCode: Int?, // Unique code for the alarm

    var created_at: Long,
    var updated_at: Long,
)