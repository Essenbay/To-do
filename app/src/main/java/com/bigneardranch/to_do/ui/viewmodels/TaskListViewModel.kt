package com.bigneardranch.to_do.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.bigneardranch.to_do.TodoApplication
import com.bigneardranch.to_do.data.database.TaskModel
import com.bigneardranch.to_do.data.database.TaskRepository
import com.bigneardranch.to_do.data.preferences.UserPreferences
import com.bigneardranch.to_do.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TasksUI(
    val tasks: List<TaskModel> = emptyList(),
    val userPreferences: UserPreferences = UserPreferences()
)

class TaskListViewModel(
    private val tasksRepository: TaskRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _tasks: MutableStateFlow<List<TaskModel>> = MutableStateFlow(emptyList())
    private val _tasksUI: MutableStateFlow<TasksUI> = MutableStateFlow(TasksUI())
    val tasksUI: StateFlow<TasksUI> = _tasksUI.asStateFlow()

    init {
        viewModelScope.launch {
            tasksRepository.getAllTasks().collect { tasks ->
                _tasks.value = tasks
                _tasksUI.update {
                    it.copy(
                        tasks = filterTasks(tasks, it.userPreferences.showCompleted)
                    )
                }
            }
        }

        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { preferences ->
                _tasksUI.update {
                    it.copy(
                        userPreferences = preferences,
                        tasks = filterTasks(_tasks.value, preferences.showCompleted)
                    )
                }
            }
        }
    }

    private fun filterTasks(
        tasks: List<TaskModel>,
        showCompleted: Boolean
    ): List<TaskModel> {
        return if (showCompleted) tasks
        else tasks.filter { !it.isSolved }
    }

    fun addTask(task: TaskModel) = viewModelScope.launch {
        tasksRepository.addTask(task)
    }

    fun updateTaskSolvedIsChecked(
        task: TaskModel, isChecked: Boolean
    ) = viewModelScope.launch {
        tasksRepository.updateTask(
            task.copy(
                isSolved = isChecked
            )
        )
    }

    fun updateTaskInProgressIsChecked(
        task: TaskModel, isChecked: Boolean
    ) = viewModelScope.launch {
        tasksRepository.updateTask(
            task.copy(
                isInProgress = isChecked
            )
        )
    }

    fun deleteTask(taskId: Int) = viewModelScope.launch {
        tasksRepository.deleteTask(tasksUI.value.tasks.findLast { it.id == taskId }
            ?: throw Exception("Task is null"))
    }

    fun deleteAllTasks() = viewModelScope.launch {
        tasksRepository.deleteAllTasks()
    }

    fun updateShowCompletedTasks() = viewModelScope.launch {
        userPreferencesRepository.updateShowCompleted(!tasksUI.value.userPreferences.showCompleted)
    }

    fun updateGetDailyNotifications() = viewModelScope.launch {
        userPreferencesRepository.updateGetDailyNotifications(!tasksUI.value.userPreferences.getDailyNotifications)
    }

    fun updateMakeSomeNotificationsPersistent() =
        viewModelScope.launch {
            userPreferencesRepository.updateMakeSomeNotificationsPersistent(
                !tasksUI.value.userPreferences.makeSomeNotificationsPersistent
            )
        }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application: TodoApplication =
                    checkNotNull(extras[APPLICATION_KEY]) as TodoApplication
                return TaskListViewModel(
                    application.repository,
                    application.userPreferencesRepository
                ) as T
            }
        }
    }
}