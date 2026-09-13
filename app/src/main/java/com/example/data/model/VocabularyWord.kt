package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary_words")
data class VocabularyWord(
    @PrimaryKey val id: String,
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val meaning: String,
    val example: String,
    val synonyms: List<String> = emptyList(),
    val category: String = "Business",
    val difficulty: String = "Intermediate",
    val masteryLevel: Int = 2, // 0 = New, 1-4 = Learning, 5 = Mastered
    val reviewCount: Int = 3,
    val lastReviewed: Long = System.currentTimeMillis()
)
