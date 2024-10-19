package com.example.goalkeeper.data.dao

import androidx.room.*
import com.example.goalkeeper.data.model.TimeEntity

    @Dao
    interface TimeDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertTime(timeEntity: TimeEntity)

        @Update
        suspend fun updateTime(timeEntity: TimeEntity)

        @Query("SELECT * FROM time_settings WHERE id = 1 LIMIT 1")
        suspend fun getTime(): TimeEntity?
    }
