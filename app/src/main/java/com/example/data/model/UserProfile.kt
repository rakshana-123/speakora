package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Chen",
    val email: String = "alex.chen@example.com",
    val nativeLanguage: String = "English",
    val learningLanguage: String = "Professional English",
    val communicationLevel: String = "Intermediate",
    val mainGoal: String = "Career & Leadership",
    val dailyMinutes: Int = 10,
    val preferredTime: String = "Morning (8:30 AM)",
    val topics: String = "Business, Technology, Public Speaking, Leadership",
    val currentStreak: Int = 18,
    val longestStreak: Int = 27,
    val freezeTokens: Int = 2,
    val xp: Int = 2850,
    val level: Int = 8,
    val levelTitle: String = "Articulate Communicator",
    val speakingScore: Int = 78,
    val listeningScore: Int = 84,
    val readingScore: Int = 88,
    val writingScore: Int = 81,
    val vocabularyScore: Int = 75,
    val grammarScore: Int = 82,
    val pronunciationScore: Int = 76,
    val confidenceScore: Int = 79,
    val avgWpm: Int = 135,
    val avgFillerWordsPerMin: Float = 2.4f,
    val isOnboarded: Boolean = true,
    val isOfflineMode: Boolean = false,
    val lastActiveDate: String = "2026-09-13",
    val streakFrozenToday: Boolean = false,
    val totalWorkoutsCompleted: Int = 42
) {
    val overallScore: Int
        get() = ((speakingScore + listeningScore + readingScore + writingScore +
                vocabularyScore + grammarScore + pronunciationScore + confidenceScore) / 8)
}
