package com.bigneardranch.to_do.data.preferences

data class UserPreferences(
    val showCompleted: Boolean = true,
    val getDailyNotifications: Boolean = false,
    val makeSomeNotificationsPersistent: Boolean = false
)