package com.example.germanflashcards.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.germanflashcards.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSync(): List<Category>

    @Insert
    suspend fun insert(category: Category)
}