package com.example.goalkeeper.di

import com.example.goalkeeper.data.Goal
import com.example.goalkeeper.data.GoalDao


open class GoalRepository(private val goalDao: GoalDao) {
    suspend fun getAllGoals(): List<Goal> {
        return goalDao.getAllGoals()
    }

    open suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

}