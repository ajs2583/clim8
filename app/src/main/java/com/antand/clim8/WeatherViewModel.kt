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

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(city)
                if (response.isSuccessful && response.body() != null) {
                    _weather.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "City not found or API error"
                    Log.e("WeatherViewModel", "API Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
                Log.e("WeatherViewModel", "Network Error: ${e.message}", e)
            }
        }
    }
}