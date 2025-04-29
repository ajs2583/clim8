package com.antand.clim8

data class WeatherResponse(
    val coord: Coord,
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Float,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Float
)
