package com.antand.clim8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.antand.clim8.databinding.FragmentForecastBinding
import android.util.Log

class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var adapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ForecastAdapter(emptyList())
        binding.recyclerForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerForecast.adapter = adapter

        val cityName = SettingsManager.getLastSearchedCity(requireContext())

        binding.textViewCityName.text = cityName

        // Fetch free 5-day forecast
        viewModel.fetchFiveDayForecast(cityName)

        viewModel.fiveDayForecast.observe(viewLifecycleOwner) { forecastResponse ->
            // Show only 7 items max (or whatever available)
            val simplifiedForecasts = forecastResponse.list.filterIndexed { index, _ -> index % 8 == 0 }.take(7)
            adapter.updateDataFromFiveDay(simplifiedForecasts)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
