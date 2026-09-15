package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val email: String = "",
    val nativeLanguage: String = "English",
    val learningLanguage: String = "Professional English",
    val communicationLevel: String = "Beginner",
    val mainGoal: String = "Career & Leadership",
    val dailyMinutes: Int = 10,
    val preferredTime: String = "Morning (8:30 AM)",
    val topics: String = "Business, Technology, Public Speaking, Leadership",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val freezeTokens: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val levelTitle: String = "Apprentice Communicator",
    val speakingScore: Int = 0,
    val listeningScore: Int = 0,
    val readingScore: Int = 0,
    val writingScore: Int = 0,
    val vocabularyScore: Int = 0,
    val grammarScore: Int = 0,
    val pronunciationScore: Int = 0,
    val confidenceScore: Int = 0,
    val avgWpm: Int = 0,
    val avgFillerWordsPerMin: Float = 0f,
    val isOnboarded: Boolean = false,
    val isOfflineMode: Boolean = false,
    val lastActiveDate: String = "",
    val streakFrozenToday: Boolean = false,
    val totalWorkoutsCompleted: Int = 0
) {
    val overallScore: Int
        get() = ((speakingScore + listeningScore + readingScore + writingScore +
                vocabularyScore + grammarScore + pronunciationScore + confidenceScore) / 8)
}
