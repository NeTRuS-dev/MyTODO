package com.example.mytodo.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(
    var name: String,
    var content: String?,
    var created_at: Long,
    var updated_at: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
