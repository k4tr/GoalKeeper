package com.example.goalkeeper.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goalkeeper.data.dao.GoalDao
import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.viewmodel.GoalViewModel

class GoalViewModelFactory(
    private val repository: GoalRepository,
    private val goalDao: GoalDao,
    private val timeDao: TimeDao,
    private val timeRepository: TimeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            return GoalViewModel(repository, goalDao, timeRepository, timeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}