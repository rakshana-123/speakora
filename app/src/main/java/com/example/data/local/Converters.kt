package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||")
    }

    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory?): String {
        return value?.name ?: ExerciseCategory.SPEAKING.name
    }

    @TypeConverter
    fun toExerciseCategory(value: String?): ExerciseCategory {
        return try {
            if (value != null) ExerciseCategory.valueOf(value) else ExerciseCategory.SPEAKING
        } catch (e: Exception) {
            ExerciseCategory.SPEAKING
        }
    }

    @TypeConverter
    fun fromExerciseType(value: ExerciseType?): String {
        return value?.name ?: ExerciseType.RECORDING.name
    }

    @TypeConverter
    fun toExerciseType(value: String?): ExerciseType {
        return try {
            if (value != null) ExerciseType.valueOf(value) else ExerciseType.RECORDING
        } catch (e: Exception) {
            ExerciseType.RECORDING
        }
    }
}
