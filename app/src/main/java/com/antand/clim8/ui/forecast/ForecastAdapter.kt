package com.antand.clim8.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load // For loading images from URLs into ImageViews
import com.antand.clim8.utils.SettingsManager
import com.antand.clim8.utils.TemperatureConverter
import com.antand.clim8.data.models.ForecastItem
import com.antand.clim8.databinding.ForecastCardBinding
import java.text.SimpleDateFormat
import java.util.*

// adapter for displaying a list of weather forecast items in a RecyclerView
class ForecastAdapter(private var forecastList: List<ForecastItem>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    // ViewHolder class that holds the binding reference for each forecast card
    inner class ForecastViewHolder(val binding: ForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    // creates a new ViewHolder by inflating the forecast_card layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ForecastCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    // binds data to each ViewHolder
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]
        val context = holder.binding.root.context

        // date formatting
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Format from API
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault()) // User-friendly format
        val date = inputFormat.parse(forecast.dt_txt)
        holder.binding.textViewDate.text = date?.let { outputFormat.format(it) } ?: forecast.dt_txt

        // temperature conversion
        val isCelsius = SettingsManager.isUsingCelsius(context) // User temperature preference

        // convert and display temperatures
        val tempMin = if (isCelsius) {
            forecast.main.temp_min.toInt()
        } else {
            TemperatureConverter.celsiusToFahrenheit(forecast.main.temp_min)
        }

        val tempMax = if (isCelsius) {
            forecast.main.temp_max.toInt()
        } else {
            TemperatureConverter.celsiusToFahrenheit(forecast.main.temp_max)
        }

        holder.binding.textViewTemp.text = "High: $tempMax° / Low: $tempMin°"

        // description Text
        holder.binding.textViewDescription.text = forecast.weather.firstOrNull()?.description ?: ""

        // weather Icon
        val iconCode = forecast.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        // load icon using Coil
        holder.binding.imageViewWeatherIcon.load(iconUrl)

        // fade-in animation
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()
    }

    // returns the number of forecast items
    override fun getItemCount() = forecastList.size

    // updates the list of forecast items and refreshes the UI
    fun updateData(newForecastList: List<ForecastItem>) {
        forecastList = newForecastList
        notifyDataSetChanged()
    }
}
