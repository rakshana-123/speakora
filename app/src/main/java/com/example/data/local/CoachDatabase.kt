package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.Achievement
import com.example.data.model.Exercise
import com.example.data.model.PracticeSession
import com.example.data.model.UserProfile
import com.example.data.model.VocabularyWord

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        VocabularyWord::class,
        PracticeSession::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CoachDatabase : RoomDatabase() {
    abstract fun coachDao(): CoachDao

    companion object {
        @Volatile
        private var INSTANCE: CoachDatabase? = null

        fun getDatabase(context: Context): CoachDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoachDatabase::class.java,
                    "ai_comm_coach_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
