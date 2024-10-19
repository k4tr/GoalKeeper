package com.example.goalkeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "goals")
    data class Goal(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        var isCompleted: Boolean = false,
        val difficulty: Difficulty,
        val isGenerated: Boolean = false,  // Поле для отметки, сгенерирована цель или нет
        val generationDate: Long? = null // Для сохранения даты генерации
    )

    enum class Difficulty {
        EASY,
        NORMAL,
        HARD
    }