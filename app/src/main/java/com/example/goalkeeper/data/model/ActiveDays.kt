package com.example.goalkeeper.data.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_days")
data class ActiveDay(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long // Сохраняется в миллисекундах
)