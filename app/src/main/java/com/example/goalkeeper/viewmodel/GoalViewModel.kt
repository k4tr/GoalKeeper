package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.data.Difficulty
import com.example.goalkeeper.data.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



// Определяем состояние ViewModel
data class GoalState(
    val goals: List<Goal> = emptyList()
)

class GoalViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    // Состояние для хранения списка целей
    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state

    // Функция для добавления цели
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            val currentGoals = _state.value.goals
            _state.value = _state.value.copy(goals = currentGoals + goal)
        }
    }

    // Функция для генерации целей
    fun generateGoals() {
        viewModelScope.launch {
            // Логика генерации целей (1 сложная, 2 средних, 3 легких)
            val difficultGoals = _state.value.goals.filter { it.difficulty == Difficulty.HARD }
            val mediumGoals = _state.value.goals.filter { it.difficulty == Difficulty.MEDIUM }
            val easyGoals = _state.value.goals.filter { it.difficulty == Difficulty.EASY }

            val generatedGoals = listOf(
                difficultGoals.randomOrNull(),  // 1 сложная цель
                mediumGoals.randomOrNull(),    // 2 средние цели
                mediumGoals.randomOrNull(),
                easyGoals.randomOrNull(),      // 3 легкие цели
                easyGoals.randomOrNull(),
                easyGoals.randomOrNull()
            ).filterNotNull() // Удаляем null

            _state.value = _state.value.copy(goals = generatedGoals)
        }
    }
}