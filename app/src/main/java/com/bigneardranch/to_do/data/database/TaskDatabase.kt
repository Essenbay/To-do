package com.bigneardranch.to_do.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val DATABASE_NAME = "task_database"

@Database(entities = [TaskModel::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext, TaskDatabase::class.java, DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .addCallback(TaskDatabaseCallback(scope))
                    .build().also { Instance = it }
            }
        }

        private class TaskDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Instance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.taskDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDao) {
            Log.d("TaskDatabase", "Database was populated")
        }
    }
}