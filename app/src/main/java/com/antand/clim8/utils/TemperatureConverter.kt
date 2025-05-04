package com.antand.clim8.utils

object TemperatureConverter {
    fun celsiusToFahrenheit(celsius: Float): Int {
        return ((celsius * 9 / 5) + 32).toInt()
    }
}
