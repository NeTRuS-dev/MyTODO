package com.example.mytodo.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val content: String?,
    val created_at: Long,
    val updated_at: Long,
)
