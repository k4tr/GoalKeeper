package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.repository.GoalRepository
import com.example.goalkeeper.data.model.Difficulty
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.data.dao.GoalDao
import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoalState(
    val goals: List<Goal?> = emptyList()
)

class GoalViewModel(
    private val repository: GoalRepository,
    private val goalDao: GoalDao,
    private val timeRepository: TimeRepository,
    private val timeDao: TimeDao,


) : ViewModel() {

    val _easyGoalsTime = MutableStateFlow(0f)
    val _mediumGoalsTime = MutableStateFlow(0f)
    val _hardGoalsTime = MutableStateFlow(0f)
    //Состояние для генерации списка целей
    private val _generatedGoals = MutableStateFlow<List<Goal>>(emptyList())
    val generatedGoals: StateFlow<List<Goal>> = _generatedGoals.asStateFlow()
    //Состояние для вывода списка целей
    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals: StateFlow<List<Goal>> = _allGoals.asStateFlow()
    // Состояние для хранения списка целей
    private val _state = MutableStateFlow(GoalState())
    val state: StateFlow<GoalState> = _state
    //Состояние для свободного времени пользователя
    private val _timeForGoals = MutableStateFlow(0) // Время, введённое пользователем
    val timeForGoals: StateFlow<Int> = _timeForGoals.asStateFlow()
    // Состояние для Toast
    private val _showToast: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast
    private val _toastMessage = MutableStateFlow("")
    val toastMessage: StateFlow<String> = _toastMessage

    init {
        loadTimeSettings()
        loadGeneratedGoals()
        checkAndClearOldGoals()
        viewModelScope.launch {
            _allGoals.value = repository.getAllGoals()
        }
    }

    private fun loadTimeSettings() {
        viewModelScope.launch {
            val timeSettings = timeDao.getTime() // Получение TimeEntity из базы данных
            _easyGoalsTime.value = timeSettings?.easyGoalsTime ?: 0f
            _mediumGoalsTime.value = timeSettings?.mediumGoalsTime ?: 0f
            _hardGoalsTime.value = timeSettings?.hardGoalsTime ?: 0f
        }
    }

    fun setTimeForGoals(hours: Int) {
        _timeForGoals.value = hours
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.updateGoal(goal)
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
            val generatedGoals = goalDao.getGeneratedGoalsForDate(today)
            _generatedGoals.value = generatedGoals // Отображаем только цели, сгенерированные на текущий день
        }
    }

    // Проверка и удаление старых сгенерированных целей
    fun checkAndClearOldGoals() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях
            goalDao.deleteOldGeneratedGoals(today)  // Удаляем сгенерированные цели за прошлые дни
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal.id)  // Здесь передается id из объекта goal
            _allGoals.value = goalDao.getAllGoals()  // Обновляем список всех целей
        }
    }
    // Функция для добавления цели
    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insertGoal(goal) // Сохраняем цель
            _allGoals.value = repository.getAllGoals() // Обновляем список всех целей
            _state.value = _state.value.copy(goals = _allGoals.value) // Обновляем состояние
            _showToast.value = true // Устанавливаем флаг
        }
    }
    // Генерация целей
    fun generateGoals() {

        viewModelScope.launch {
            val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)  // Текущая дата в днях

            // Получаем настройки времени для каждой категории задач
            val timeSettings = timeDao.getTime() // Получаем TimeEntity из базы
            val easyTimeLimit = timeSettings?.easyGoalsTime ?: 0f
            val mediumTimeLimit = timeSettings?.mediumGoalsTime ?: 0f
            val hardTimeLimit = timeSettings?.hardGoalsTime ?: 0f

            // Среднее время выполнения одной цели по категориям (в минутах)
            val avgEasyGoalTime = 10f // примерное среднее время для лёгкой цели
            val avgMediumGoalTime = 15f // для средней цели
            val avgHardGoalTime = 30f // для сложной цели

            // Рассчитываем количество целей для каждой категории на основе времени
            val easyGoalsCount = (easyTimeLimit / avgEasyGoalTime).toInt()
            val mediumGoalsCount = (mediumTimeLimit / avgMediumGoalTime).toInt()
            val hardGoalsCount = (hardTimeLimit / avgHardGoalTime).toInt()

            // Генерация целей по каждой категории
            val difficultGoals = goalDao.getGoalsByDifficulty(Difficulty.HARD)
                .shuffled()
                .take(hardGoalsCount) // Ограничиваем количество до расчётного значения
            // проверка для toast
            if ((mediumGoalsCount > goalDao.getGoalsByDifficulty(Difficulty.NORMAL).size) || (easyGoalsCount > goalDao.getGoalsByDifficulty(Difficulty.EASY).size) || (hardGoalsCount > goalDao.getGoalsByDifficulty(Difficulty.HARD).size)){
                // Устанавливаем сообщение для Toast
                _toastMessage.value = "Недостаточно введенных целей для корректной генерации"
                _showToast.value = true // Показываем Toast
               return@launch
            }

            val mediumGoals = goalDao.getGoalsByDifficulty(Difficulty.NORMAL)
                .shuffled()
                .take(mediumGoalsCount) // Ограничиваем количество до расчётного значения

            val easyGoals = goalDao.getGoalsByDifficulty(Difficulty.EASY)
                .shuffled()
                .take(easyGoalsCount) // Ограничиваем количество до расчётного значения

            // Собираем все цели для генерации
            val newGeneratedGoals = difficultGoals + mediumGoals + easyGoals

            // Обновляем дату генерации и статус
            val finalGoals = newGeneratedGoals.map {
                it.copy(isGenerated = true, generationDate = today) // Добавляем текущую дату
            }

            // Удаляем старые и добавляем новые цели
            goalDao.deleteOldGeneratedGoals(today)
            goalDao.insertGoals(finalGoals)
            _generatedGoals.value = finalGoals // Обновляем состояние
        }
    }
    fun resetToastFlag() {
        _showToast.value = false
    }
}
