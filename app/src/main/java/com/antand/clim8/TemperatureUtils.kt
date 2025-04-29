package com.antand.clim8.util

object TemperatureUtils {
    fun celsiusToFahrenheit(celsius: Float): Int {
        return ((celsius * 9 / 5) + 32).toInt()
    }
}
