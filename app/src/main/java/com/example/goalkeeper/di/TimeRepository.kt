package com.example.goalkeeper.di

import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.data.model.TimeEntity

class TimeRepository(private val timeDao: TimeDao) {
    suspend fun insertTime(timeEntity: TimeEntity) {
        timeDao.insertTime(timeEntity)
    }

    suspend fun updateTime(timeEntity: TimeEntity) {
        timeDao.updateTime(timeEntity)
    }
    suspend fun saveUserTime(userTime: Float) {
        val existingTime = timeDao.getTime()
        if (existingTime != null) {
            // Если запись существует, обновляем её
            val updatedTime = existingTime.copy(userTime = userTime)
            timeDao.updateTime(updatedTime)
        } else {
            // Если записи нет, создаем новую
            val timeEntity = TimeEntity(userTime = userTime)
            timeDao.insertTime(timeEntity)
        }
    }
    suspend fun getTime(): TimeEntity? {
        return timeDao.getTime()
    }
}
