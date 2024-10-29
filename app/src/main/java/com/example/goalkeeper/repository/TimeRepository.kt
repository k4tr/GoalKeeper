package com.example.goalkeeper.repository

import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.data.model.TimeEntity

class TimeRepository(private val timeDao: TimeDao) {
    suspend fun insertTime(timeEntity: TimeEntity) {
        timeDao.insertTime(timeEntity)
    }

    suspend fun updateTime(timeEntity: TimeEntity) {
        timeDao.updateTime(timeEntity)
    }
    suspend fun saveUserTime(
        userTime: Float,
        easyGoalsTime: Float,
        mediumGoalsTime: Float,
        hardGoalsTime: Float,
        savedRightBoundaryHard: Float
    ) {
        val existingTime = timeDao.getTime()
        if (existingTime != null) {
            // Если запись существует, обновляем её
            val updatedTime = existingTime.copy(
                userTime = userTime,
                easyGoalsTime = easyGoalsTime,
                mediumGoalsTime = mediumGoalsTime,
                hardGoalsTime = hardGoalsTime,
                savedRightBoundaryHard = savedRightBoundaryHard
            )
            timeDao.updateTime(updatedTime)
        } else {
            // Если записи нет, создаем новую
            val timeEntity = TimeEntity(
                userTime = userTime,
                easyGoalsTime = easyGoalsTime,
                mediumGoalsTime = mediumGoalsTime,
                hardGoalsTime = hardGoalsTime,
                savedRightBoundaryHard = savedRightBoundaryHard
            )
            timeDao.insertTime(timeEntity)
        }
    }
    suspend fun getTime(): TimeEntity? {
        return timeDao.getTime()
    }
}
