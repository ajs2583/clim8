package com.antand.clim8.data.repository

import com.antand.clim8.data.models.FiveDayForecastResponse
import com.antand.clim8.data.remote.RetrofitClient

class WeatherRepository {

    private val api = RetrofitClient.instance

    private val apiKey = ""

    suspend fun getWeather(city: String) =
        api.getCurrentWeather(city, apiKey, units = "metric")

    suspend fun getFiveDayForecast(city: String): FiveDayForecastResponse {
        return api.getFiveDayForecast(cityName = city, apiKey = apiKey, units = "metric")
    }

}
