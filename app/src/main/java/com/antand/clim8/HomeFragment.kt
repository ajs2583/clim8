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

    private var currentCityName: String? = null // Track latest searched city

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

        // Setup RecyclerView
        adapter = FavoriteCityAdapter(emptyList(),
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

        // Observe favorites from database
        database.favoriteCityDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNotEmpty()) {
                binding.textFavoritesTitle.visibility = View.VISIBLE
                binding.recyclerFavorites.visibility = View.VISIBLE
                adapter.updateData(favorites)
            } else {
                binding.textFavoritesTitle.visibility = View.GONE
                binding.recyclerFavorites.visibility = View.GONE
            }
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

        // Weather success observer
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            showLoading(false)

            val context = requireContext()
            val isCelsius = SettingsManager.isUsingCelsius(context)

            binding.cityName.apply {
                text = weather.name
                visibility = View.VISIBLE
            }

            binding.tempText.apply {
                text = if (isCelsius) {
                    "${weather.main.temp}°C"
                } else {
                    "${celsiusToFahrenheit(weather.main.temp)}°F"
                }
                visibility = View.VISIBLE
            }

            binding.descText.apply {
                text = weather.weather[0].description
                visibility = View.VISIBLE
            }

            binding.weatherIcon.apply {
                val iconCode = weather.weather[0].icon
                val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                load(iconUrl)
                visibility = View.VISIBLE
            }

            binding.humidityText.apply {
                text = "Humidity: ${weather.main.humidity}%"
                visibility = View.VISIBLE
            }

            binding.pressureText.apply {
                text = "Pressure: ${weather.main.pressure} hPa"
                visibility = View.VISIBLE
            }

            binding.windSpeedText.apply {
                text = "Wind Speed: ${weather.wind.speed} m/s"
                visibility = View.VISIBLE
            }


            currentCityName = weather.name // Save the searched city

            // Show Favorite Button
            binding.buttonFavoriteCity.visibility = View.VISIBLE
        }

        // Error observer
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            showLoading(false)
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        // Handle "Favorite this city" button
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

    private fun celsiusToFahrenheit(celsius: Float): Int {
        return ((celsius * 9 / 5) + 32).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
