package com.example.data.repository

import com.example.data.local.CoachDao
import com.example.data.local.SeedData
import com.example.data.model.Achievement
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseResult
import com.example.data.model.PracticeSession
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CoachRepository(private val dao: CoachDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allExercises: Flow<List<Exercise>> = dao.getAllExercises()
    val vocabularyList: Flow<List<VocabularyWord>> = dao.getAllVocabulary()
    val practiceSessions: Flow<List<PracticeSession>> = dao.getAllSessions()
    val achievements: Flow<List<Achievement>> = dao.getAllAchievements()

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        val currentProfile = dao.getUserProfileOnce()
        if (currentProfile == null) {
            dao.insertUserProfile(UserProfile())
            dao.insertExercises(SeedData.exercises)
            dao.insertVocabulary(SeedData.vocabularyList)
            dao.insertAchievements(SeedData.achievements)
        }
    }

    suspend fun getTodayWorkout(targetMinutes: Int): List<Exercise> = withContext(Dispatchers.IO) {
        val exercises = dao.getAllExercises().firstOrNull() ?: SeedData.exercises
        val profile = dao.getUserProfileOnce() ?: UserProfile()

        // Adaptive recommendation based on weakest scores
        val weakestCategory = when {
            profile.speakingScore <= profile.vocabularyScore && profile.speakingScore <= profile.listeningScore -> ExerciseCategory.SPEAKING
            profile.vocabularyScore <= profile.listeningScore -> ExerciseCategory.VOCABULARY
            profile.listeningScore <= profile.grammarScore -> ExerciseCategory.LISTENING
            else -> ExerciseCategory.SCENARIO
        }

        val count = when (targetMinutes) {
            5 -> 2
            10 -> 4
            15 -> 5
            else -> 6
        }

        val prioritized = mutableListOf<Exercise>()

        // 1. Weakest category exercise first
        val weakEx = exercises.firstOrNull { it.category == weakestCategory }
        if (weakEx != null) prioritized.add(weakEx)

        // 2. Vocabulary drill
        val vocabEx = exercises.firstOrNull { it.category == ExerciseCategory.VOCABULARY && !prioritized.contains(it) }
        if (vocabEx != null) prioritized.add(vocabEx)

        // 3. Speaking / Pronunciation drill
        val speakEx = exercises.firstOrNull { (it.category == ExerciseCategory.SPEAKING || it.category == ExerciseCategory.PRONUNCIATION) && !prioritized.contains(it) }
        if (speakEx != null) prioritized.add(speakEx)

        // 4. Scenario or Difficult Conversation
        val scenEx = exercises.firstOrNull { (it.category == ExerciseCategory.SCENARIO || it.category == ExerciseCategory.DIFFICULT_CONVERSATION) && !prioritized.contains(it) }
        if (scenEx != null) prioritized.add(scenEx)

        // 5. Fill remaining if needed
        for (ex in exercises) {
            if (prioritized.size >= count) break
            if (!prioritized.contains(ex)) {
                prioritized.add(ex)
            }
        }

        prioritized
    }

    suspend fun submitExerciseResult(exercise: Exercise, result: ExerciseResult): UserProfile = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce() ?: UserProfile()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Calculate XP and level
        val newXp = profile.xp + result.xpEarned
        val newLevel = 1 + (newXp / 350)
        val levelTitle = when {
            newLevel >= 15 -> "Master Orator"
            newLevel >= 10 -> "Executive Communicator"
            newLevel >= 7 -> "Articulate Communicator"
            newLevel >= 4 -> "Developing Speaker"
            else -> "Apprentice Communicator"
        }

        // Calculate updated streak
        val isFirstSessionToday = profile.lastActiveDate != todayStr
        val newStreak = if (isFirstSessionToday) profile.currentStreak + 1 else profile.currentStreak
        val bestStreak = maxOf(newStreak, profile.longestStreak)

        // Update score based on category
        var speaking = profile.speakingScore
        var listening = profile.listeningScore
        var reading = profile.readingScore
        var writing = profile.writingScore
        var vocab = profile.vocabularyScore
        var grammar = profile.grammarScore
        var pronunciation = profile.pronunciationScore
        var confidence = profile.confidenceScore

        when (exercise.category) {
            ExerciseCategory.SPEAKING, ExerciseCategory.PUBLIC_SPEAKING -> {
                speaking = ((speaking * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
                confidence = ((confidence * 0.85) + (result.score * 0.15)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.PRONUNCIATION -> {
                pronunciation = ((pronunciation * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.LISTENING -> {
                listening = ((listening * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.READING -> {
                reading = ((reading * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.WRITING -> {
                writing = ((writing * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.VOCABULARY -> {
                vocab = ((vocab * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.GRAMMAR -> {
                grammar = ((grammar * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
            }
            ExerciseCategory.SCENARIO, ExerciseCategory.DIFFICULT_CONVERSATION, ExerciseCategory.NON_VERBAL -> {
                confidence = ((confidence * 0.8) + (result.score * 0.2)).toInt().coerceIn(40, 100)
                speaking = ((speaking * 0.85) + (result.score * 0.15)).toInt().coerceIn(40, 100)
            }
        }

        // Record session
        dao.insertSession(
            PracticeSession(
                date = todayStr,
                category = exercise.category.displayName,
                exerciseTitle = exercise.title,
                durationSec = exercise.durationSec,
                score = result.score,
                xpEarned = result.xpEarned,
                fillerCount = result.fillerCount,
                wpm = result.wpm,
                feedback = result.feedback
            )
        )

        val updatedProfile = profile.copy(
            xp = newXp,
            level = newLevel,
            levelTitle = levelTitle,
            currentStreak = newStreak,
            longestStreak = bestStreak,
            lastActiveDate = todayStr,
            totalWorkoutsCompleted = profile.totalWorkoutsCompleted + 1,
            speakingScore = speaking,
            listeningScore = listening,
            readingScore = reading,
            writingScore = writing,
            vocabularyScore = vocab,
            grammarScore = grammar,
            pronunciationScore = pronunciation,
            confidenceScore = confidence
        )

        dao.insertUserProfile(updatedProfile)
        updatedProfile
    }

    suspend fun useStreakFreeze(): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce() ?: return@withContext false
        if (profile.freezeTokens > 0 && !profile.streakFrozenToday) {
            val updated = profile.copy(
                freezeTokens = profile.freezeTokens - 1,
                streakFrozenToday = true
            )
            dao.insertUserProfile(updated)
            return@withContext true
        }
        false
    }

    suspend fun recoverStreakChallenge(): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfileOnce() ?: return@withContext false
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updated = profile.copy(
            currentStreak = maxOf(profile.currentStreak, 18),
            lastActiveDate = todayStr,
            streakFrozenToday = false
        )
        dao.insertUserProfile(updated)
        true
    }

    suspend fun updateProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(profile)
    }

    suspend fun updateVocabularyMastery(wordId: String, correct: Boolean) = withContext(Dispatchers.IO) {
        val words = dao.getAllVocabulary().firstOrNull() ?: return@withContext
        val word = words.firstOrNull { it.id == wordId } ?: return@withContext
        val newLevel = if (correct) (word.masteryLevel + 1).coerceAtMost(5) else (word.masteryLevel - 1).coerceAtLeast(0)
        dao.updateVocabulary(
            word.copy(
                masteryLevel = newLevel,
                reviewCount = word.reviewCount + 1,
                lastReviewed = System.currentTimeMillis()
            )
        )
    }
}
