package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Achievement
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.PracticeSession
import com.example.data.model.UserAccount
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyWord
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachDao {
    // User Accounts (login)
    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserAccount(username: String): UserAccount?

    @Query("SELECT * FROM user_accounts ORDER BY createdAt ASC")
    suspend fun getAllUserAccounts(): List<UserAccount>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUserAccount(account: UserAccount): Long

    @Query("SELECT COUNT(*) FROM user_accounts WHERE role = 'ADMIN'")
    suspend fun countAdmins(): Int

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Exercises
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE category = :category")
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: String): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    // Vocabulary
    @Query("SELECT * FROM vocabulary_words ORDER BY masteryLevel ASC, lastReviewed ASC")
    fun getAllVocabulary(): Flow<List<VocabularyWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(words: List<VocabularyWord>)

    @Update
    suspend fun updateVocabulary(word: VocabularyWord)

    // Practice Sessions
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<PracticeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession): Long

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, currentProgress DESC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)
}
