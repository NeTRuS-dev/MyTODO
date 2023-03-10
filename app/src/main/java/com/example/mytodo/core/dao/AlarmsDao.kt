package com.example.mytodo.core.dao

import androidx.room.*
import com.example.mytodo.core.models.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {
    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getAlarmById(id: Int): Alarm?

    @Query("SELECT * FROM alarm WHERE noteId = :noteId")
    fun getAlarmsForNoteId(noteId: Int): Flow<List<Alarm>>

    @Query("SELECT * FROM alarm WHERE noteId = :noteId")
    fun getAlarmsForNoteIdOnce(noteId: Int): List<Alarm>

    @Query("SELECT * FROM alarm")
    fun getAllAlarmsOnce(): List<Alarm>

    @Insert
    suspend fun insertAll(vararg alarms: Alarm)

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)
}