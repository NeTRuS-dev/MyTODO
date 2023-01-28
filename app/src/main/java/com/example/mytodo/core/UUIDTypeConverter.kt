package com.example.mytodo.core

import androidx.room.TypeConverter
import java.util.*

class UUIDTypeConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}