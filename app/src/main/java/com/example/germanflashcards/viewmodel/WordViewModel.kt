package com.example.germanflashcards.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.germanflashcards.data.dao.CategoryDao
import com.example.germanflashcards.data.dao.WordDao
import com.example.germanflashcards.data.model.Category
import com.example.germanflashcards.data.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WordViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = (application as com.example.germanflashcards.FlashcardApplication).database
    private val wordDao: WordDao = database.wordDao()
    private val categoryDao: CategoryDao = database.categoryDao()
    
    fun getWordsByCategory(categoryId: Long): Flow<List<Word>> = wordDao.getWordsByCategory(categoryId)
    
    fun getWordsByStatus(isLearned: Boolean): Flow<List<Word>> = wordDao.getWordsByStatus(isLearned)
    
    val categories: Flow<List<Category>> = categoryDao.getAllCategories()

    fun addWord(word: Word) = viewModelScope.launch {
        wordDao.insert(word)
    }

    fun updateWord(word: Word) = viewModelScope.launch {
        wordDao.update(word)
    }
    
    fun deleteWord(word: Word) = viewModelScope.launch {
        wordDao.delete(word)
    }

    // Методы для работы с категориями
    fun insertCategory(category: Category) = viewModelScope.launch {
        categoryDao.insert(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        categoryDao.update(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        categoryDao.delete(category)
    }

    fun getCategoryByName(name: String): Flow<Category?> = categoryDao.getCategoryByName(name)

    fun getWordsByCategoryAndStatus(categoryId: Long, isLearned: Boolean): Flow<List<Word>> = 
        wordDao.getWordsByCategoryAndStatus(categoryId, isLearned)

    // Синхронный метод для получения категорий
    fun getCategoriesSync(): List<Category> = runBlocking {
        categoryDao.getAllCategoriesSync()
    }
}
