package com.example.mytodo.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    var name: String,
    var content: String?,
    var created_at: Long,
    var updated_at: Long,
    @ColumnInfo(defaultValue = "0")
    var is_done: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
