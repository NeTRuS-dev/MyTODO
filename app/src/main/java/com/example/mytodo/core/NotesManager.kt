package com.example.mytodo.core

import com.example.mytodo.core.dao.AlarmsDao
import com.example.mytodo.core.dao.NotesDao
import com.example.mytodo.core.models.Alarm
import com.example.mytodo.core.models.Note
import javax.inject.Inject

class NotesManager @Inject constructor(
    private val notesDao: NotesDao,
    private val alarmsDao: AlarmsDao
) {
    suspend fun getNoteById(id: Int) = notesDao.getNoteById(id)
    fun getAlarmsByNoteId(id: Int) = alarmsDao.getAlarmsForNoteId(id)
    fun getAlarmsForNoteIdOnce(id: Int) = alarmsDao.getAlarmsForNoteIdOnce(id)
    fun getAllNotes() = notesDao.getAll()
    suspend fun addNewNote(note: Note) = notesDao.insertAll(note)
    fun getAllAlarmsOnce() = alarmsDao.getAllAlarmsOnce()
    suspend fun updateNote(note: Note) = notesDao.updateAll(note)
    suspend fun addAlarm(alarm: Alarm) = alarmsDao.insertAll(alarm)
    suspend fun deleteNote(note: Note) = notesDao.delete(note)
    suspend fun deleteAlarm(note: Note?, alarm: Alarm) {
        note?.let {
            if (alarm.noteId == it.id) {
                alarmsDao.delete(alarm)
            }
        }
    }
}