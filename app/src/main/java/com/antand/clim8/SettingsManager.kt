package com.antand.clim8

import android.content.Context
import android.content.SharedPreferences

object SettingsManager {
    private const val PREFS_NAME = "user_settings"
    private const val KEY_USE_CELSIUS = "use_celsius"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_UPDATE_FREQUENCY = "update_frequency"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUseCelsius(context: Context, useCelsius: Boolean) {
        prefs(context).edit().putBoolean(KEY_USE_CELSIUS, useCelsius).apply()
    }

    fun isUsingCelsius(context: Context): Boolean =
        prefs(context).getBoolean(KEY_USE_CELSIUS, true) // Default = true

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun areNotificationsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    fun setUpdateFrequency(context: Context, hours: Int) {
        prefs(context).edit().putInt(KEY_UPDATE_FREQUENCY, hours).apply()
    }

    fun getUpdateFrequency(context: Context): Int =
        prefs(context).getInt(KEY_UPDATE_FREQUENCY, 6) // Default = 6 hours

    private const val KEY_LAST_CITY = "last_city"

    fun setLastSearchedCity(context: Context, city: String) {
        prefs(context).edit().putString(KEY_LAST_CITY, city).apply()
    }

    fun getLastSearchedCity(context: Context): String {
        return prefs(context).getString(KEY_LAST_CITY, "Flagstaff") ?: "Flagstaff"
    }

}
