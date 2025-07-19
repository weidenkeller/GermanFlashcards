package com.example.germanflashcards.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.germanflashcards.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSync(): List<Category>

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    fun getCategoryByName(name: String): Flow<Category?>

    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)
}