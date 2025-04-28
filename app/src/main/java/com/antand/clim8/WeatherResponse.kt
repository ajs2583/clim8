package com.antand.clim8

data class WeatherResponse(
    val coord: Coord, // ✅ Add this line
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind // ✅ Also make sure you added Wind earlier for the forecast polish
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
