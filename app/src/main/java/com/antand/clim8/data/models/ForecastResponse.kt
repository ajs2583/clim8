package com.antand.clim8.data.models

data class ForecastResponse(
    val daily: List<DailyForecast>
)

data class DailyForecast(
    val dt: Long, // timestamp
    val temp: Temp,
    val weather: List<Weather>
)

data class Temp(
    val day: Float,
    val min: Float,
    val max: Float
)