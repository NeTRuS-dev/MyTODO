package com.example.mytodo.core.dao

import androidx.room.*
import com.example.mytodo.core.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM note ORDER BY updated_at DESC")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM note WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Note?

    @Insert
    suspend fun insertAll(vararg notes: Note)

    @Update
    suspend fun updateAll(vararg notes: Note)

    @Delete
    suspend fun delete(note: Note)
}