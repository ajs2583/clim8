package com.antand.clim8

import com.antand.clim8.RetrofitClient

class WeatherRepository {
    private val api = RetrofitClient.instance
    private val apiKey = "136e9be3e4d8cd6a52fa2c7109282ab1" // Replace with your OpenWeatherAPI key

    suspend fun getWeather(city: String) = api.getCurrentWeather(city, apiKey)
}