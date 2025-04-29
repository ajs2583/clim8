package com.antand.clim8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.antand.clim8.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var database: FavoriteCityDatabase
    private lateinit var adapter: FavoriteCityAdapter

    private var currentCityName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FavoriteCityDatabase.getDatabase(requireContext())

        adapter = FavoriteCityAdapter(
            emptyList(),
            onCityClicked = { cityName ->
                showLoading(true)
                viewModel.fetchWeather(cityName)
            },
            onDeleteClicked = { favoriteCity ->
                lifecycleScope.launch {
                    database.favoriteCityDao().delete(favoriteCity)
                    Toast.makeText(requireContext(), "${favoriteCity.cityName} removed from favorites", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter

        database.favoriteCityDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            binding.textFavoritesTitle.visibility = if (favorites.isNotEmpty()) View.VISIBLE else View.GONE
            binding.recyclerFavorites.visibility = if (favorites.isNotEmpty()) View.VISIBLE else View.GONE
            adapter.updateData(favorites)
        }

        binding.buttonGetWeather.setOnClickListener {
            val city = binding.editTextCity.text.toString().trim()
            if (city.isNotEmpty()) {
                showLoading(true)
                viewModel.fetchWeather(city)
                Toast.makeText(requireContext(), "Fetching weather for $city", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            showLoading(false)

            val context = requireContext()
            val isCelsius = SettingsManager.isUsingCelsius(context)

            binding.cityName.text = weather.name
            binding.tempText.text = if (isCelsius) {
                "${weather.main.temp}°C"
            } else {
                "${TemperatureConverter.celsiusToFahrenheit(weather.main.temp)}°F"
            }
            binding.descText.text = weather.weather.firstOrNull()?.description ?: ""
            binding.weatherIcon.load("https://openweathermap.org/img/wn/${weather.weather.firstOrNull()?.icon}@2x.png")
            binding.humidityText.text = "Humidity: ${weather.main.humidity}%"
            binding.pressureText.text = "Pressure: ${weather.main.pressure} hPa"
            binding.windSpeedText.text = "Wind Speed: ${weather.wind.speed} m/s"

            binding.buttonFavoriteCity.visibility = View.VISIBLE
            currentCityName = weather.name
            SettingsManager.setLastSearchedCity(requireContext(), weather.name)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            showLoading(false)
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonFavoriteCity.setOnClickListener {
            currentCityName?.let { cityName ->
                lifecycleScope.launch {
                    val exists = database.favoriteCityDao().cityExists(cityName)
                    if (exists == 0) {
                        val favorite = FavoriteCity(cityName = cityName)
                        database.favoriteCityDao().insert(favorite)
                        Toast.makeText(requireContext(), "$cityName added to favorites!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "$cityName is already in favorites!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
