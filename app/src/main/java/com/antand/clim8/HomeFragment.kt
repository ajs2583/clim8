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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FavoriteCityDatabase.getDatabase(requireContext())

        adapter = FavoriteCityAdapter(emptyList(),
            onCityClicked = { city ->
                binding.editTextCity.setText(city)
                fetchWeather(city)
            },
            onDeleteClicked = { favorite ->
                lifecycleScope.launch {
                    database.favoriteCityDao().delete(favorite)
                    Toast.makeText(requireContext(), "${favorite.cityName} removed from favorites", Toast.LENGTH_SHORT).show()
                }
            })

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter

        database.favoriteCityDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            val visible = favorites.isNotEmpty()
            binding.textFavoritesTitle.visibility = if (visible) View.VISIBLE else View.GONE
            binding.recyclerFavorites.visibility = if (visible) View.VISIBLE else View.GONE
            adapter.updateData(favorites)
        }

        binding.buttonGetWeather.setOnClickListener {
            val city = binding.editTextCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
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
                "${weather.main.temp.toInt()}°C"
            } else {
                "${celsiusToFahrenheit(weather.main.temp)}°F"
            }

            binding.descText.text = weather.weather.firstOrNull()?.description ?: ""
            binding.weatherIcon.load("https://openweathermap.org/img/wn/${weather.weather.firstOrNull()?.icon}@2x.png")
            binding.humidityText.text = "Humidity: ${weather.main.humidity}%"
            binding.pressureText.text = "Pressure: ${weather.main.pressure} hPa"
            binding.windSpeedText.text = "Wind Speed: ${weather.wind.speed} m/s"

            // Show UI elements
            listOf(
                binding.cityName,
                binding.tempText,
                binding.descText,
                binding.weatherIcon,
                binding.humidityText,
                binding.pressureText,
                binding.windSpeedText,
                binding.buttonFavoriteCity
            ).forEach { it.visibility = View.VISIBLE }

            currentCityName = weather.name
            SettingsManager.setLastSearchedCity(requireContext(), currentCityName!!)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            showLoading(false)
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonFavoriteCity.setOnClickListener {
            currentCityName?.let { city ->
                lifecycleScope.launch {
                    val exists = database.favoriteCityDao().cityExists(city)
                    if (exists == 0) {
                        database.favoriteCityDao().insert(FavoriteCity(cityName = city))
                        Toast.makeText(requireContext(), "$city added to favorites!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "$city is already a favorite", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Auto-load last city
        val lastCity = SettingsManager.getLastSearchedCity(requireContext())
        if (lastCity.isNotBlank()) {
            binding.editTextCity.setText(lastCity)
            fetchWeather(lastCity)
        }
    }

    private fun fetchWeather(city: String) {
        showLoading(true)
        viewModel.fetchWeather(city)
        Toast.makeText(requireContext(), "Fetching weather for $city", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun celsiusToFahrenheit(celsius: Float): Int {
        return ((celsius * 9 / 5) + 32).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
