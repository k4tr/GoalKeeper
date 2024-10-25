package com.example.goalkeeper.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.goalkeeper.data.dao.GoalDao
import com.example.goalkeeper.data.dao.TimeDao
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.data.model.TimeEntity

@Database(entities = [Goal::class, TimeEntity::class], version = 10, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun timeDao(): TimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goal_database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}
val MIGRATION_3_4 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Поле isGenerated должно быть NOT NULL, что корректно
        database.execSQL("ALTER TABLE goals ADD COLUMN isGenerated INTEGER NOT NULL DEFAULT 0")

        // Поле generationDate должно быть nullable, уберите NOT NULL
        database.execSQL("ALTER TABLE goals ADD COLUMN generationDate INTEGER DEFAULT NULL")
    }
}
