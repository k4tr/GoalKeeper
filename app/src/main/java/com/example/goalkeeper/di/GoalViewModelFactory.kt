package com.example.goalkeeper.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goalkeeper.data.dao.GoalDao
import com.example.goalkeeper.viewmodel.GoalViewModel

class GoalViewModelFactory(
    private val repository: GoalRepository,
    private val goalDao: GoalDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            return GoalViewModel(repository, goalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}