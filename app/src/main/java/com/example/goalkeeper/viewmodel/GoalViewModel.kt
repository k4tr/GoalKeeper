package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.data.model.Difficulty
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.data.dao.GoalDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoalState(
    val goals: List<Goal?> = emptyList()
)

class GoalViewModel(
    private val repository: GoalRepository,
    private val goalDao: GoalDao
) : ViewModel() {
    //Состояние для генерации списка целей
    private val _generatedGoals = MutableStateFlow<List<Goal>>(emptyList())
    val generatedGoals: StateFlow<List<Goal>> = _generatedGoals
    //Состояние для вывода списка целей
    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals: StateFlow<List<Goal>> = _allGoals.asStateFlow()
    // Состояние для хранения списка целей
    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state
    //Состояние для свободного времени пользователя
    private val _timeForGoals = MutableStateFlow(0) // Время, введённое пользователем
    val timeForGoals: StateFlow<Int> = _timeForGoals.asStateFlow()

    init {
        loadGeneratedGoals()
        checkAndClearOldGoals()
        viewModelScope.launch {
            _allGoals.value = repository.getAllGoals()
        }
    }

    fun setTimeForGoals(hours: Int) {
        _timeForGoals.value = hours
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.updateGoal(goal) // Предполагаем, что у вас есть метод обновления в DAO
            _generatedGoals.value = _generatedGoals.value.map {
                if (it.id == goal.id) goal else it // Обновляем список с новым состоянием
            }
        }
    }

    // Обработчик изменения состояния чекбокса
    fun onGoalCheckedChange(goal: Goal, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(isCompleted = isChecked)  // Обновляете состояние цели
            repository.updateGoal(updatedGoal)  // Обновляете цель в базе данных
            _generatedGoals.value = _generatedGoals.value.map {
                if (it.id == goal.id) updatedGoal else it  // Обновляем список целей
            }
        }
    }

    // Загрузка сгенерированных целей на сегодня
    fun loadGeneratedGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях
            val savedGoals = goalDao.getGeneratedGoalsForDate(today)
            if (savedGoals.isNotEmpty()) {
                _generatedGoals.value = savedGoals // Загружаем сохраненные цели, если они есть
            }
        }
    }

    // Проверка и удаление старых сгенерированных целей
    fun checkAndClearOldGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях
            goalDao.deleteOldGeneratedGoals(today)  // Удаляем сгенерированные цели за прошлые дни
        }
    }
    // Функция для добавления цели
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insertGoal(goal) // Сохраняем цель
            _allGoals.value = repository.getAllGoals() // Обновляем список всех целей
            _state.value = _state.value.copy(goals = _allGoals.value) // Обновляем состояние
        }
    }

    // Генерация целей
    fun generateGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях

            val difficultGoals = goalDao.getGoalsByDifficulty(Difficulty.HARD)
            val mediumGoals = goalDao.getGoalsByDifficulty(Difficulty.NORMAL)
            val easyGoals = goalDao.getGoalsByDifficulty(Difficulty.EASY)

            val selectedDifficultGoal = difficultGoals.randomOrNull()
            val selectedMediumGoals = mediumGoals.shuffled().take(2)
            val selectedEasyGoals = easyGoals.shuffled().take(2)

            val newGeneratedGoals = listOfNotNull(
                selectedDifficultGoal
            ) + selectedMediumGoals + selectedEasyGoals

            val finalGoals = newGeneratedGoals.map {
                it.copy(isGenerated = true, generationDate = today)
            }

            // Очищаем ранее сгенерированные цели
            goalDao.deleteOldGeneratedGoals(today)

            // Вставляем новые сгенерированные цели
            goalDao.insertGoals(finalGoals)
            _generatedGoals.value = finalGoals
        }
    }
    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal.id)  // Здесь передается id из объекта goal
            _allGoals.value = goalDao.getAllGoals()  // Обновляем список всех целей
        }
    }
}
