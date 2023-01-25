package com.example.mytodo.core

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mytodo.core.dao.AlarmsDao
import com.example.mytodo.core.dao.NotesDao
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.core.models.Note

@Database(
    version = 1,
    entities = [Note::class, Alarm::class],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NotesDao
    abstract fun alarmsDao(): AlarmsDao
}