package com.example.goalkeeper.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Goal::class], version = 2, exportSchema = false)
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
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE goals ADD COLUMN isGenerated INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE goals ADD COLUMN generationDate INTEGER NOT NULL DEFAULT 0")
    }
}
