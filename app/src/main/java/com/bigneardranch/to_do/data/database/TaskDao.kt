package com.bigneardranch.to_do.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskModel>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Int): Flow<TaskModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskModel)

    @Update
    suspend fun updateTask(task: TaskModel)

    @Delete
    suspend fun deleteTask(task: TaskModel)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}