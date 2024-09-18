package com.example.goalkeeper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GoalDao {
    @Insert
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>

    @Query("SELECT * FROM goals WHERE difficulty = :difficulty")
    suspend fun getGoalsByDifficulty(difficulty: Difficulty): List<Goal>

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Long)
}