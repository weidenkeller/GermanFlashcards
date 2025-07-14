package com.example.germanflashcards.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val russian: String, // Слово на русском
    val german: String,  // Слово на немецком
    val imageUri: String?, // URI изображения из галереи (если есть)
    val categoryId: Long,   // ID категории
    val isLearned: Boolean = false, // Флаг: выучено/невыучено
    val createdAt: Long = System.currentTimeMillis() // Время создания
)