package com.antand.clim8

class WeatherRepository {

    private val api = RetrofitClient.instance

    private val apiKey = "136e9be3e4d8cd6a52fa2c7109282ab1"

    suspend fun getWeather(city: String) =
        api.getCurrentWeather(city, apiKey, units = "metric")

    suspend fun getFiveDayForecast(city: String): FiveDayForecastResponse =
        api.getFiveDayForecast(city, apiKey, units = "metric")
}
