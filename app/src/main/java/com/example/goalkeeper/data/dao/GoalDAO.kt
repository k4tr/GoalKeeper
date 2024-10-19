package com.example.goalkeeper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.goalkeeper.data.model.Difficulty
import com.example.goalkeeper.data.model.Goal

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE isGenerated = 1 AND generationDate = :date")
    suspend fun getGeneratedGoalsForDate(date: Long): List<Goal>

    @Query("DELETE FROM goals WHERE isGenerated = 1 AND generationDate < :currentDate")
    suspend fun deleteOldGeneratedGoals(currentDate: Long): Int

    @Update
    suspend fun updateGoal(goal: Goal) // Метод для обновления цел

    @Query("DELETE FROM goals WHERE isGenerated = 1")
    suspend fun deleteAllGeneratedGoals()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<Goal>)

    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>

    @Query("SELECT * FROM goals WHERE difficulty = :difficulty")
    suspend fun getGoalsByDifficulty(difficulty: Difficulty): List<Goal>

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Insert
    suspend fun insert(it: Goal)
}