package com.antand.clim8.ui.home

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
import com.antand.clim8.utils.SettingsManager
import com.antand.clim8.ui.viewmodel.WeatherViewModel
import com.antand.clim8.data.local.FavoriteCityDatabase
import com.antand.clim8.data.models.FavoriteCity
import com.antand.clim8.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

// home frag is responsible for showing current weather and managing favorite cities
class HomeFragment : Fragment() {

    // view binding to interact with the layout
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // viewmodel to manage weather data
    private val viewModel: WeatherViewModel by viewModels()

    // local database reference for storing favorite cities
    private lateinit var database: FavoriteCityDatabase

    // adapter for displaying the list of favorite cities
    private lateinit var adapter: FavoriteCityAdapter

    // holds the currently displayed city name
    private var currentCityName: String? = null

    // inflates the view from XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // called after the view is created — main setup logic
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize the database instance
        database = FavoriteCityDatabase.getDatabase(requireContext())

        // setup the adapter for favorite cities RecyclerView
        adapter = FavoriteCityAdapter(
            emptyList(),
            onCityClicked = { city ->
                // when a city is clicked, populate input and fetch weather
                binding.editTextCity.setText(city)
                fetchWeather(city)
            },
            onDeleteClicked = { favorite ->
                // when delete is clicked, remove from DB and show toast
                lifecycleScope.launch {
                    database.favoriteCityDao().delete(favorite)
                    Toast.makeText(requireContext(), "${favorite.cityName} removed from favorites", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // recyclerView setup
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter

        // bbserve changes to the favorite cities table
        database.favoriteCityDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            val visible = favorites.isNotEmpty()
            // show/hide UI elements based on if there are favorites
            binding.textFavoritesTitle.visibility = if (visible) View.VISIBLE else View.GONE
            binding.recyclerFavorites.visibility = if (visible) View.VISIBLE else View.GONE
            adapter.updateData(favorites)
        }

        // "Get Weather" button functionality
        binding.buttonGetWeather.setOnClickListener {
            val city = binding.editTextCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        // observe weather data from ViewModel
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            showLoading(false)

            val context = requireContext()
            val isCelsius = SettingsManager.isUsingCelsius(context)

            // update UI with weather details
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

            // make weather details visible
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

            // update last searched city and currentCityName
            currentCityName = weather.name
            SettingsManager.setLastSearchedCity(requireContext(), currentCityName!!)
        }

        // observe error messages and display them as Toasts
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            showLoading(false)
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        // handle "Add to Favorites" button click
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

        // auto-fetch weather for last searched city when fragment loads
        val lastCity = SettingsManager.getLastSearchedCity(requireContext())
        if (lastCity.isNotBlank()) {
            binding.editTextCity.setText(lastCity)
            fetchWeather(lastCity)
        }
    }

    // triggers weather data fetch and shows loading indicator
    private fun fetchWeather(city: String) {
        showLoading(true)
        viewModel.fetchWeather(city)
        Toast.makeText(requireContext(), "Fetching weather for $city", Toast.LENGTH_SHORT).show()
    }

    // controls visibility of the progress bar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // helper function to convert Celsius to Fahrenheit
    private fun celsiusToFahrenheit(celsius: Float): Int {
        return ((celsius * 9 / 5) + 32).toInt()
    }

    // clear the view binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
