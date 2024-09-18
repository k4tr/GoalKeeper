package com.example.goalkeeper.data

import androidx.room.TypeConverter

class DifficultyConverter {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return Difficulty.valueOf(value)
    }
}