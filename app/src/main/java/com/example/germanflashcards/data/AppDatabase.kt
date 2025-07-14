package com.example.germanflashcards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.germanflashcards.data.dao.CategoryDao
import com.example.germanflashcards.data.dao.WordDao
import com.example.germanflashcards.data.model.Category
import com.example.germanflashcards.data.model.Word

@Database(entities = [Word::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flashcard_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}