package com.example.goalkeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "goals")
    data class Goal(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        var isCompleted: Boolean = false,
        val difficulty: Difficulty,
        val isGenerated: Boolean = true,  // Поле для отметки, сгенерирована цель или нет
        val generationDate: Long? = System.currentTimeMillis() / (1000 * 60 * 60 * 24) // Для сохранения даты генерации
    )

    enum class Difficulty {
        EASY,
        NORMAL,
        HARD
    }