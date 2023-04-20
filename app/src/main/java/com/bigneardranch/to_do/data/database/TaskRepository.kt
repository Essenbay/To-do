package com.bigneardranch.to_do.data.database

import com.bigneardranch.to_do.data.database.TaskDao
import com.bigneardranch.to_do.data.database.TaskModel
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao){
    fun getAllTasks(): Flow<List<TaskModel>> = taskDao.getAllTasks()

    fun getTask(id: Int): Flow<TaskModel?> = taskDao.getTask(id)

    suspend fun updateTask(taskModel: TaskModel) = taskDao.updateTask(taskModel)

    suspend fun addTask(taskModel: TaskModel) = taskDao.addTask(taskModel)

    suspend fun deleteTask(taskModel: TaskModel) = taskDao.deleteTask(taskModel)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}