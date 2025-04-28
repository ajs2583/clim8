package com.antand.clim8

data class FiveDayForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: MainForecast,
    val weather: List<Weather>
)

data class MainForecast(
    val temp: Float,
    val temp_min: Float,
    val temp_max: Float
)
