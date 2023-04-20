package com.bigneardranch.to_do

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.preferencesDataStore
import com.bigneardranch.to_do.data.database.TaskDatabase
import com.bigneardranch.to_do.data.database.TaskRepository
import com.bigneardranch.to_do.data.notifications.CurrentTaskNotification
import com.bigneardranch.to_do.data.preferences.UserPreferencesRepository
import com.bigneardranch.to_do.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

private const val USER_PREFERENCES_NAME = "user_preferences"

class TodoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { TaskDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TaskRepository(database.taskDao()) }
    private val datastore by preferencesDataStore(name = USER_PREFERENCES_NAME)
    val userPreferencesRepository by lazy {
        UserPreferencesRepository(
            datastore,
            applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        CurrentTaskNotification.createNotificationChannel(this)
    }
}