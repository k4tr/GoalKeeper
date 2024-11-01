package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.data.model.TimeEntity
import com.example.goalkeeper.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimeViewModel(private val repository: TimeRepository) : ViewModel() {
    private val _timeSettings = MutableStateFlow<TimeEntity?>(null)
    val timeSettings: StateFlow<TimeEntity?> = _timeSettings

    private val _userTime = MutableStateFlow(0f) // StateFlow для хранения времени
    val userTime: StateFlow<Float> get() = _userTime
    private val _easyGoalsTime = MutableStateFlow(0f)
    val easyGoalsTime: StateFlow<Float> get() = _easyGoalsTime
    private val _mediumGoalsTime = MutableStateFlow(0f)
    val mediumGoalsTime: StateFlow<Float> get() = _mediumGoalsTime
    private val _hardGoalsTime = MutableStateFlow(0f)
    val hardGoalsTime: StateFlow<Float> get() = _hardGoalsTime
    private val _savedRightBoundaryHard = MutableStateFlow(360f)
    val savedRightBoundaryHard: StateFlow<Float> get() = _savedRightBoundaryHard

    init {
        // Загружаем данные из базы при инициализации
        viewModelScope.launch {
            val timeEntity = repository.getTime()
            if (timeEntity != null) {
                _userTime.value = timeEntity.userTime
                _easyGoalsTime.value = timeEntity.easyGoalsTime
                _mediumGoalsTime.value = timeEntity.mediumGoalsTime
                _hardGoalsTime.value = timeEntity.hardGoalsTime
                _timeSettings.value = timeEntity
                _savedRightBoundaryHard.value = timeEntity.savedRightBoundaryHard
            } else {
                // Создаем запись с дефолтным значением, если она отсутствует
                val defaultTime = 60f
                val defaultEasyGoalsTime = 20f
                val defaultMediumGoalsTime = 20f
                val defaultHardGoalsTime = 20f
                val savedRightBoundaryHard = 360f
                repository.saveUserTime(defaultTime, defaultEasyGoalsTime, defaultMediumGoalsTime, defaultHardGoalsTime, savedRightBoundaryHard)
                _userTime.value = defaultTime
                _easyGoalsTime.value = defaultEasyGoalsTime
                _mediumGoalsTime.value = defaultMediumGoalsTime
                _hardGoalsTime.value = defaultHardGoalsTime
                _timeSettings.value = TimeEntity(
                    userTime = defaultTime,
                    easyGoalsTime = defaultEasyGoalsTime,
                    mediumGoalsTime = defaultMediumGoalsTime,
                    hardGoalsTime = defaultHardGoalsTime,
                    savedRightBoundaryHard = savedRightBoundaryHard
                )
            }
        }
    }

    // Метод для обновления времени
    fun updateUserTime(
        newUserTime: Float,
        newEasyGoalsTime: Float,
        newMediumGoalsTime: Float,
        newHardGoalsTime: Float,
        savedRightBoundaryHard: Float
    ) {
        _userTime.value = newUserTime
        _easyGoalsTime.value = newEasyGoalsTime
        _mediumGoalsTime.value = newMediumGoalsTime
        _hardGoalsTime.value = newHardGoalsTime
        _savedRightBoundaryHard.value = savedRightBoundaryHard
        viewModelScope.launch {
            repository.saveUserTime(newUserTime, newEasyGoalsTime, newMediumGoalsTime, newHardGoalsTime, savedRightBoundaryHard)
        }
    }

    // Метод для асинхронного получения времени
    suspend fun getUserTime(): TimeEntity? {
        return repository.getTime()
    }
}
