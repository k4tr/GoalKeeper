package com.example.goalkeeper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val difficulty: Difficulty,
    val isGenerated: Boolean = false,  // Поле для отметки, сгенерирована цель или нет
    val generationDate: Long = 0 // Для сохранения даты генерации
)

enum class Difficulty {
    EASY,
    NORMAL,
    HARD
}