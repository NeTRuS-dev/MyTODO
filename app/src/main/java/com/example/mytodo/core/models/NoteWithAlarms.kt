package com.example.mytodo.core.models

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithAlarms(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id", entityColumn = "noteId"
    ) val alarms: List<Alarm>,
)
