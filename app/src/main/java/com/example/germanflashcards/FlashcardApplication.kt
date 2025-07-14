package com.example.germanflashcards

import android.app.Application
import com.example.germanflashcards.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlashcardApplication : Application() {
    
    lateinit var database: AppDatabase
        private set
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        
        // Инициализируем категории
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = database.categoryDao()
            val categories = categoryDao.getAllCategoriesSync()
            if (categories.isEmpty()) {
                listOf("Еда", "Животные", "Глаголы").forEach { name ->
                    categoryDao.insert(com.example.germanflashcards.data.model.Category(name = name))
                }
            }
        }
    }
}