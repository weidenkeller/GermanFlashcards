package com.example.germanflashcards.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.germanflashcards.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE categoryId = :categoryId")
    fun getWordsByCategory(categoryId: Long): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isLearned = :isLearned")
    fun getWordsByStatus(isLearned: Boolean): Flow<List<Word>>

    @Insert
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)
}