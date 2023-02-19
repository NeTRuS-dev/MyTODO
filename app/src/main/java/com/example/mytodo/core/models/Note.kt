package com.example.mytodo.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var content: String?,
    var created_at: Long,
    var updated_at: Long,
    @ColumnInfo(name = "is_done", defaultValue = "0")
    var is_done: Boolean = false
)
