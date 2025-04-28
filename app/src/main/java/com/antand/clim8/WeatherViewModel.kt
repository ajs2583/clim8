package com.antand.clim8

import android.util.Log
import androidx.lifecycle.*
import com.antand.clim8.WeatherResponse
import com.antand.clim8.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> = _weather

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _fiveDayForecast = MutableLiveData<FiveDayForecastResponse>()
    val fiveDayForecast: LiveData<FiveDayForecastResponse> = _fiveDayForecast



    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(city)
                if (response.isSuccessful && response.body() != null) {
                    val weather = response.body()!!
                    _weather.value = weather
                    _error.value = null


                } else {
                    _error.value = "City not found or API error"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    fun fetchFiveDayForecast(city: String) {
        viewModelScope.launch {
            try {
                val forecastResponse = repository.getFiveDayForecast(city)
                _fiveDayForecast.value = forecastResponse
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Five Day Forecast Error: ${e.localizedMessage}")
            }
        }
    }



}