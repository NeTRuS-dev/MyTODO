package com.example.mytodo.core

import com.example.mytodo.core.dao.NotesDao
import com.example.mytodo.core.models.Note
import java.util.*
import javax.inject.Inject

class NotesManager @Inject constructor(private val notesDao: NotesDao) {
    fun getNoteById(id: Int) = notesDao.getNoteById(id)
    fun getAllNotes() = notesDao.getAll()
}