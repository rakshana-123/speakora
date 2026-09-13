package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val category: String,
    val exerciseTitle: String,
    val durationSec: Int,
    val score: Int,
    val xpEarned: Int,
    val fillerCount: Int = 0,
    val wpm: Int = 0,
    val feedback: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val xpReward: Int = 100
)

data class CommunityRoom(
    val id: String,
    val title: String,
    val topic: String,
    val description: String,
    val participantsCount: Int,
    val hostName: String,
    val isLive: Boolean = true,
    val tags: List<String> = emptyList(),
    val roomType: String = "Speaking Club"
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String> = emptyList(),
    val analysisSnippet: String? = null
)
