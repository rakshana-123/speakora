package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseCategory(val displayName: String, val iconName: String) {
    VOCABULARY("Vocabulary", "Spellcheck"),
    LISTENING("Listening", "Headphones"),
    SPEAKING("Speaking", "Mic"),
    PRONUNCIATION("Pronunciation", "RecordVoiceOver"),
    GRAMMAR("Grammar", "MenuBook"),
    READING("Reading", "Article"),
    WRITING("Writing", "EditNote"),
    SCENARIO("Roleplay Scenario", "Forum"),
    PUBLIC_SPEAKING("Public Speaking", "CoPresent"),
    DIFFICULT_CONVERSATION("Difficult Conversations", "Psychology"),
    NON_VERBAL("Non-Verbal & Presence", "Visibility")
}

enum class ExerciseType {
    MULTIPLE_CHOICE,
    FILL_BLANK,
    RECORDING,
    PRONUNCIATION_REPEAT,
    ROLEPLAY_CONVERSATION,
    WRITING_RESPONSE,
    LISTENING_COMPREHENSION,
    SPEED_READING
}

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: ExerciseCategory,
    val type: ExerciseType,
    val difficulty: String = "Intermediate",
    val durationSec: Int = 120,
    val instructions: String,
    val promptContent: String,
    val audioScript: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = "",
    val xpReward: Int = 30,
    val tags: String = "general",
    val sampleIdealAnswer: String = ""
)

data class ExerciseResult(
    val exerciseId: String,
    val score: Int,
    val userResponse: String,
    val transcript: String = "",
    val wpm: Int = 0,
    val fillerCount: Int = 0,
    val fillerWordsDetected: List<String> = emptyList(),
    val feedback: String,
    val strengths: List<String> = emptyList(),
    val areasToImprove: List<String> = emptyList(),
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
