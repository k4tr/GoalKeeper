package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.data.Difficulty
import com.example.goalkeeper.data.Goal
import com.example.goalkeeper.data.GoalDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



// Определяем состояние ViewModel
data class GoalState(
    val goals: List<Goal?> = emptyList()
)

class GoalViewModel(
    private val repository: GoalRepository,
    private val goalDao: GoalDao
) : ViewModel() {

    fun getAllGoals() {
        viewModelScope.launch {
            val goals = repository.getAllGoals()
            // Здесь вы можете обновить состояние или выполнить другие действия с полученными целями
        }
    }

    private val _generatedGoals = MutableStateFlow<List<Goal>>(emptyList())
    val generatedGoals: StateFlow<List<Goal>> = _generatedGoals
    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals: StateFlow<List<Goal>> = _allGoals

    // Состояние для хранения списка целей
    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state

    init {
        loadGeneratedGoals()
        checkAndClearOldGoals()
        viewModelScope.launch {
            _allGoals.value = repository.getAllGoals()
        }
    }

    fun loadGeneratedGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях
            val savedGoals = goalDao.getGeneratedGoalsForDate(today)

            if (savedGoals.isNotEmpty()) {
                _generatedGoals.value = savedGoals // Загружаем сохраненные цели, если они есть
            }
        }
    }
    //проверка даты и удаление списка
    fun checkAndClearOldGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
            val lastGeneratedGoals = goalDao.getGeneratedGoalsForDate(today - 1)

            if (lastGeneratedGoals.isNotEmpty()) {
                goalDao.deleteAllGeneratedGoals()
            }
        }
    }
    // Функция для добавления цели
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insertGoal(goal) // Сначала сохраняем цель в базе данных
            val updatedGoals = repository.getAllGoals() // Обновляем список целей
            _state.value = _state.value.copy(goals = updatedGoals)
        }
    }

    //генерация списка целей
    fun generateGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях

            // Получаем уже сгенерированные цели для сегодняшнего дня
            val existingGoals = goalDao.getGeneratedGoalsForDate(today)

            if (existingGoals.isNotEmpty()) {
                _generatedGoals.value = existingGoals
            } else {
                // Если нет сгенерированных целей на сегодня, создаем новые
                val difficultGoals = goalDao.getAllGoals().filter { it.difficulty == Difficulty.HARD }
                val mediumGoals = goalDao.getAllGoals().filter { it.difficulty == Difficulty.NORMAL }
                val easyGoals = goalDao.getAllGoals().filter { it.difficulty == Difficulty.EASY }

                // Случайно выбираем 1 сложную цель, 2 средних и 3 простых
                val selectedDifficultGoal = difficultGoals.randomOrNull()
                val selectedMediumGoals = mediumGoals.shuffled().take(2)
                val selectedEasyGoals = easyGoals.shuffled().take(3)

                // Объединяем выбранные цели
                val newGeneratedGoals = listOfNotNull(
                    selectedDifficultGoal
                ) + selectedMediumGoals + selectedEasyGoals

                // Добавляем информацию о генерации
                val finalGoals = newGeneratedGoals.map { it.copy(isGenerated = true, generationDate = today) }

                // Сохраняем сгенерированные цели в базе данных
                goalDao.insertGoals(finalGoals)
                _generatedGoals.value = finalGoals
            }
        }
    }

    fun clearGeneratedGoals() {
        viewModelScope.launch {
            goalDao.deleteAllGeneratedGoals()
            _generatedGoals.value = emptyList()
        }
    }
}