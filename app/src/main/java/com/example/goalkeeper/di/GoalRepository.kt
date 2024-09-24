package com.example.goalkeeper.di

import com.example.goalkeeper.data.Goal
import com.example.goalkeeper.data.GoalDao


open class GoalRepository(private val goalDao: GoalDao) {
    open suspend fun getAllGoals() = goalDao.getAllGoals()

    open suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

}