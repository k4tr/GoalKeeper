package com.example.goalkeeper.repository

import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.data.dao.GoalDao


open class GoalRepository(private val goalDao: GoalDao) {
    suspend fun getAllGoals(): List<Goal> {
        return goalDao.getAllGoals()
    }

    suspend fun insertGoal(goal: Goal) {
        val goalToInsert = if (goal.isGenerated) goal.copy(generationDate = System.currentTimeMillis()) else goal
        goalDao.insertGoal(goalToInsert)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

}