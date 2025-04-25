package com.antand.clim8

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.antand.clim8.databinding.FragmentHomeBinding
import coil.load

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            binding.cityName.apply {
                text = weather.name
                visibility = View.VISIBLE
            }
            binding.tempText.apply {
                text = "${weather.main.temp}°C"
                visibility = View.VISIBLE
            }
            binding.descText.apply {
                text = weather.weather[0].description
                visibility = View.VISIBLE
            }

            val iconCode = weather.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
            binding.weatherIcon.apply {
                load(iconUrl)
                visibility = View.VISIBLE
            }
        }

        // Error observer
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            showLoading(false)
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
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