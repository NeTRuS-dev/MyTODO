package com.example.mytodo.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mytodo.core.dao.NotesDao
import com.example.mytodo.core.models.Note

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NotesDao
}