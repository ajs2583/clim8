package com.antand.clim8.ui.viewmodel

import androidx.lifecycle.*
import com.antand.clim8.data.models.FiveDayForecastResponse
import com.antand.clim8.data.models.WeatherResponse
import com.antand.clim8.data.repository.WeatherRepository
import kotlinx.coroutines.launch

// ViewModel for managing weather data and business logic
class WeatherViewModel : ViewModel() {

    // instance of the repository that handles api communication
    private val repository = WeatherRepository()

    // backing property for current weather data (MutableLiveData is private)
    private val _weather = MutableLiveData<WeatherResponse>()

    // publicly exposed LiveData to observe from UI
    val weather: LiveData<WeatherResponse> = _weather

    // backing property for error messages
    private val _error = MutableLiveData<String?>()
    // public LiveData for error handling
    val error: LiveData<String?> = _error

    // backing property for 5-day forecast data
    private val _fiveDayForecast = MutableLiveData<FiveDayForecastResponse>()
    // public LiveData for forecast observation
    val fiveDayForecast: LiveData<FiveDayForecastResponse> = _fiveDayForecast

    // fetch current weather

    // fetches current weather for the given city using coroutines
    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = repository.getWeather(city)
                if (response.isSuccessful && response.body() != null) {
                    _weather.value = response.body()       // post weather to observers
                    _error.value = null                    // clear previous errors
                } else {
                    _error.value = "City not found or API error" // API returned error
                }
            } catch (e: Exception) {
                // catch network or unexpected errors
                _error.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    // fetch 5 day forecast

    // fetches 5 day forecast for the given city
    fun fetchFiveDayForecast(city: String) {
        viewModelScope.launch {
            try {
                val forecastResponse = repository.getFiveDayForecast(city)
                _fiveDayForecast.value = forecastResponse  // post forecast to observers
            } catch (e: Exception) {
                _error.value = "Failed to fetch forecast: ${e.localizedMessage}" // error during API call
            }
        }
    }
}
