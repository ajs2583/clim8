package com.antand.clim8

data class ForecastResponse(
    val daily: List<DailyForecast>
)

data class DailyForecast(
    val dt: Long,
    val temp: Temp,
    val weather: List<Weather>
)

data class Temp(
    val day: Float,
    val min: Float,
    val max: Float
)
