package com.example.goalkeeper.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Goal::class], version = 3, exportSchema = false)
@TypeConverters(DifficultyConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goal_database"
                )
                    .fallbackToDestructiveMigration()  // Удаление старой базы данных и создание новой при изменении схемы
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Поле isGenerated должно быть NOT NULL, что корректно
        database.execSQL("ALTER TABLE goals ADD COLUMN isGenerated INTEGER NOT NULL DEFAULT 0")

        // Поле generationDate должно быть nullable, уберите NOT NULL
        database.execSQL("ALTER TABLE goals ADD COLUMN generationDate INTEGER DEFAULT NULL")
    }
}
