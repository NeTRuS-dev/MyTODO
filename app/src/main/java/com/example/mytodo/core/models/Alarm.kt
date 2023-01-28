package com.example.mytodo.core.models

import androidx.room.*
import com.example.mytodo.core.UUIDTypeConverter
import java.util.Date
import java.util.UUID

@Entity
@TypeConverters(UUIDTypeConverter::class)
data class Alarm(
    var notification_time: Long,
    var noteId: Int,
    var alarmCode: Int?, // Unique code for the alarm

    var created_at: Long,
    var updated_at: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}