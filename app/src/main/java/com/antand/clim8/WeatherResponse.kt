package com.antand.clim8

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind // ✅ ADD this
)

data class Main(
    val temp: Float,
    val pressure: Int, // ✅ ADD this
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Float // ✅ ADD new Wind class
)
