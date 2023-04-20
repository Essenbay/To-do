package com.bigneardranch.to_do.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val TAG = "UserPreferencesRepository"

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>, context: Context) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { preferences ->
            val showCompleted = preferences[PreferencesKeys.SHOW_COMPLETED] ?: false
            val getDailyNotifications = preferences[PreferencesKeys.GET_DAILY_NOTIFICATIONS] ?: true
            val makeSomeNotificationsPersistent =
                preferences[PreferencesKeys.MAKE_SOME_NOTIFICATIONS_PERSISTENT] ?: true
            UserPreferences(showCompleted, getDailyNotifications, makeSomeNotificationsPersistent)
        }

    suspend fun updateShowCompleted(showCompleted: Boolean) = try {
        dataStore.edit {
            it[PreferencesKeys.SHOW_COMPLETED] = showCompleted
        }
    } catch (e: IOException) {
        Log.d(TAG, e.message ?: "Exception occurred in $TAG: ${e}")
    }

    suspend fun updateGetDailyNotifications(getDailyNotifications: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.GET_DAILY_NOTIFICATIONS] = getDailyNotifications
        }
    }

    suspend fun updateMakeSomeNotificationsPersistent(makeSomeNotificationsPersistent: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.MAKE_SOME_NOTIFICATIONS_PERSISTENT] = makeSomeNotificationsPersistent
        }
    }
}