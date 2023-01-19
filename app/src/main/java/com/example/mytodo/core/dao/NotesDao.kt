package com.example.mytodo.core.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mytodo.core.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM note ORDER BY updated_at DESC")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNoteById(id: Int): Note

    @Query("SELECT * FROM note WHERE id IN (:noteIds)")
    fun loadAllByIds(noteIds: IntArray): List<Note>

    @Query("SELECT * FROM note WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Note

    @Insert
    fun insertAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)
}