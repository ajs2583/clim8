package com.antand.clim8.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.antand.clim8.utils.SettingsManager
import com.antand.clim8.ui.viewmodel.WeatherViewModel
import com.antand.clim8.databinding.FragmentForecastBinding

// fragment responsible for displaying the 5-day weather forecast
class ForecastFragment : Fragment() {

    // view binding to interact with the fragment_forecast.xml layout
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    // viewmodel instance to access weather data
    private val viewModel: WeatherViewModel by viewModels()

    // recyclerview adapter for displaying forecast items
    private lateinit var adapter: ForecastAdapter

    // called to inflate the layout for the fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    // called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize the adapter with an empty list
        adapter = ForecastAdapter(emptyList())

        // set up RecyclerView with vertical linear layout
        binding.recyclerForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerForecast.adapter = adapter

        // retrieve the last searched city name from settings and show it
        val cityName = SettingsManager.getLastSearchedCity(requireContext())
        binding.textViewCityName.text = cityName

        // ask the ViewModel to fetch the 5-day forecast for the city
        viewModel.fetchFiveDayForecast(cityName)

        // observe changes to the 5-day forecast LiveData
        viewModel.fiveDayForecast.observe(viewLifecycleOwner) { forecastResponse ->
            // simplify the 3-hour interval forecast to roughly one entry per day (every 8th item)
            val simplifiedForecasts = forecastResponse.list
                .filterIndexed { index, _ -> index % 8 == 0 }
                // limit to 7 entries for a clean outcome
                .take(7)

            // update the adapter with the filtered forecast data
            adapter.updateData(simplifiedForecasts)
        }
    }

    // clean up view binding reference when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
