package com.antand.clim8.utils

import android.content.Context
import android.content.SharedPreferences

// singleton object to manage user settings using SharedPreferences
object SettingsManager {

    // name of the SharedPreferences file
    private const val PREFS_NAME = "user_settings"

    // keys for different settings
    private const val KEY_USE_CELSIUS = "use_celsius"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_UPDATE_FREQUENCY = "update_frequency"
    private const val KEY_LAST_CITY = "last_city"

    // helper method to get the SharedPreferences instance
    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // temperature unit setting

    // save whether the user prefers Celsius
    fun setUseCelsius(context: Context, useCelsius: Boolean) {
        prefs(context).edit().putBoolean(KEY_USE_CELSIUS, useCelsius).apply()
    }

    // get the user's preference for temperature unit (default is true = Celsius)
    fun isUsingCelsius(context: Context): Boolean =
        prefs(context).getBoolean(KEY_USE_CELSIUS, true)

    // notifications settings

    // save whether notifications are enabled
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    // check if notifications are enabled (default is true)
    fun areNotificationsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    // update frequency setting

    // save the weather update frequency in hours
    fun setUpdateFrequency(context: Context, hours: Int) {
        prefs(context).edit().putInt(KEY_UPDATE_FREQUENCY, hours).apply()
    }

    // get the update frequency (default is 6 hours)
    fun getUpdateFrequency(context: Context): Int =
        prefs(context).getInt(KEY_UPDATE_FREQUENCY, 6)

    // last searched city setting

    // save the last searched city name
    fun setLastSearchedCity(context: Context, city: String) {
        prefs(context).edit().putString(KEY_LAST_CITY, city).apply()
    }

    // retrieve the last searched city (default is "Flagstaff")
    fun getLastSearchedCity(context: Context): String {
        return prefs(context).getString(KEY_LAST_CITY, "Flagstaff") ?: "Flagstaff"
    }
}
