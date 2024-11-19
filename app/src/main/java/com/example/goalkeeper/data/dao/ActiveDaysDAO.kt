package com.example.goalkeeper.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalkeeper.data.dao.entity.ActiveDay
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveDayDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActiveDay(activeDay: ActiveDay)

    @Query("SELECT * FROM active_days")
    fun getAllActiveDays(): Flow<List<ActiveDay>>

    @Query("DELETE FROM active_days WHERE date = :date")
    suspend fun deleteActiveDay(date: Long)
}