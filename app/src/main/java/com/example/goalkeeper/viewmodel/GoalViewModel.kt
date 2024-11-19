package com.example.goalkeeper.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.data.dao.ActiveDayDao
import com.example.goalkeeper.repository.GoalRepository
import com.example.goalkeeper.data.dao.GoalDao
import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.data.dao.entity.ActiveDay
import com.example.goalkeeper.data.model.Difficulty
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.repository.TimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class GoalState(
    val goals: List<Goal?> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
class GoalViewModel(
    private val repository: GoalRepository,
    private val goalDao: GoalDao,
    private val timeRepository: TimeRepository,
    private val timeDao: TimeDao,
    private val activeDayDao: ActiveDayDao
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

    private val _easyGoalsCount = MutableLiveData<Int>()
    val easyGoalsCount: LiveData<Int> get() = _easyGoalsCount

    private val _activeDays = MutableStateFlow<List<Long>>(emptyList()) // список с датами активности в миллисекундах
    val activeDays: StateFlow<List<Long>> = activeDayDao.getAllActiveDays()
        .map { days -> days.map { it.date } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

    // Обработчик изменения состояния чекбокса
    @RequiresApi(Build.VERSION_CODES.O)
    fun onGoalCheckedChange(goal: Goal, isCompleted: Boolean) {
        viewModelScope.launch {
            // Обновляем цель в базе данных
            goalDao.updateGoal(goal.copy(isCompleted = isCompleted))

            val allGoals = goalDao.getAllGoals()
            // Получаем текущую дату
            val startOfDayMillis = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val endOfDayMillis = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            // Загружаем все цели для текущего дня
            // Перезагружаем все цели на текущий день из базы данных
            val todayGoals = allGoals.filter {
                val generationDateMillis = it.generationDate?.times(24)?.times(60)?.times(60)
                    ?.times(1000L)
                generationDateMillis == startOfDayMillis
            }
            Log.d("GoalCheck", "Today goals after manual filtering: $todayGoals")
            // Лог для проверки данных
            // Проверяем, есть ли хотя бы одна выполненная цель
            val anyGoalCompleted = todayGoals.any { it.isCompleted }
            Log.d("GoalCheck", "Any goal completed: $anyGoalCompleted")

            // Если цель выполнена, добавляем текущий день в таблицу активных дней
            if (anyGoalCompleted) {
                activeDayDao.insertActiveDay(ActiveDay(date = startOfDayMillis))
            }
            else{
                if (activeDays.value.contains(startOfDayMillis)) {
                    activeDayDao.deleteActiveDay(startOfDayMillis)
                }
            }

            // Перезагружаем список целей из базы данных, чтобы обновить UI
            _generatedGoals.value = _generatedGoals.value.map {
                if (it.id == goal.id) it.copy(isCompleted = isCompleted) else it
            }
        }
    }

    // Загрузка сгенерированных целей на сегодня
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadGeneratedGoals() {
        viewModelScope.launch {
            val todayDays = System.currentTimeMillis() / (1000 * 60 * 60 * 24) // Текущая дата в днях
            val generatedGoals = goalDao.getGeneratedGoalsForDate(todayDays, todayDays + 1)
            _generatedGoals.value = generatedGoals
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
