package com.example.mytodo.core

import com.example.mytodo.core.dao.NotesDao
import com.example.mytodo.core.models.Note
import java.util.*
import javax.inject.Inject

class NotesManager @Inject constructor(private val notesDao: NotesDao) {
    suspend fun getNoteById(id: Int) = notesDao.getNoteById(id)
    fun getAllNotes() = notesDao.getAll()
    suspend fun addNewNote(note: Note) = notesDao.insertAll(note)
    suspend fun updateNote(note: Note) = notesDao.updateAll(note)

    suspend fun deleteNote(note: Note) = notesDao.delete(note)
}