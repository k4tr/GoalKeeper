package com.example.goalkeeper.di

import com.example.goalkeeper.data.GoalDao


class GoalRepository(private val goalDao: GoalDao) {
    suspend fun getAllGoals() = goalDao.getAllGoals()

    // suspend fun insertGoal(goal: Goal) {
    //     goalDao.insert(goal)
    // }

    // suspend fun deleteGoal(goal: Goal) {
    //     goalDao.delete(goal)
    // }
}