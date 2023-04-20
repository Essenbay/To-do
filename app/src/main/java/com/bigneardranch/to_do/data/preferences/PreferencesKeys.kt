package com.bigneardranch.to_do.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val GET_DAILY_NOTIFICATIONS = booleanPreferencesKey("get_daily_notifications")
    val MAKE_SOME_NOTIFICATIONS_PERSISTENT = booleanPreferencesKey("make_some_notifications_persistent")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
}