package com.example.goalkeeper.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_settings")
data class TimeEntity(
    @PrimaryKey val id: Int = 1,
    val userTime: Float // Время, введённое пользователем в минутах
)