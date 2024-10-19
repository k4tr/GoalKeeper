package com.example.goalkeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalkeeper.data.model.TimeEntity
import com.example.goalkeeper.di.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimeViewModel(private val repository: TimeRepository) : ViewModel() {
    private val _timeSettings = MutableStateFlow<TimeEntity?>(null)
    val timeSettings: StateFlow<TimeEntity?> = _timeSettings

    private val _userTime = MutableStateFlow(0f) // Используйте StateFlow для хранения времени
    val userTime: StateFlow<Float> get() = _userTime

    init {
        // Загружаем данные из базы при инициализации
        viewModelScope.launch {
            val timeEntity = repository.getTime()
            if (timeEntity != null) {
                _userTime.value = timeEntity.userTime
                _timeSettings.value = timeEntity
            } else {
                // Создаем запись с дефолтным значением, если она отсутствует
                val defaultTime = 60f
                repository.saveUserTime(defaultTime)
                _userTime.value = defaultTime
                _timeSettings.value = TimeEntity(userTime = defaultTime)
            }
        }
    }

    // Метод для обновления времени
    fun updateUserTime(newTime: Float) {
        _userTime.value = newTime
        viewModelScope.launch {
            repository.saveUserTime(newTime)
        }
    }

    // Метод для асинхронного получения времени
    suspend fun getUserTime(): Float {
        val timeEntity = repository.getTime()
        return timeEntity?.userTime ?: 60f
    }
//    init {
//        // Загружаем данные из базы при инициализации
//        viewModelScope.launch {
//            _userTime.value = repository.getTime()?.userTime ?: 0f
//            _timeSettings.value = repository.getTime()
//        }
//    }
//
//    // Метод для обновления времени
//    fun updateUserTime(newTime: Float) {
//        _userTime.value = newTime // Обновление времени
//        // Сохраните новое значение в базе данных внутри корутины
//        viewModelScope.launch {
//            repository.saveUserTime(newTime)
//        }
//    }
//
//    fun getUserTime(): Float {
//        var time = 60f
//        viewModelScope.launch {
//            val timeEntity = repository.getTime()
//            if (timeEntity != null) {
//                time = timeEntity.userTime
//            } else {
//                val defaultTime = 60f
//                repository.updateTime(TimeEntity(userTime = defaultTime))
//                time = defaultTime
//            }
//        }
//        return time
//    }
}
