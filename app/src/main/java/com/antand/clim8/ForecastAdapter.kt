package com.antand.clim8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.antand.clim8.databinding.ForecastCardBinding
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private var forecastList: List<ForecastItem>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(val binding: ForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ForecastCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]
        val context = holder.binding.root.context

        // Format date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = inputFormat.parse(forecast.dt_txt)
        holder.binding.textViewDate.text = date?.let { outputFormat.format(it) } ?: forecast.dt_txt

        // Temperature conversion
        val isCelsius = SettingsManager.isUsingCelsius(context)
        val temp = if (isCelsius) {
            forecast.main.temp.toInt()
        } else {
            TemperatureConverter.celsiusToFahrenheit(forecast.main.temp)
        }

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
        holder.binding.textViewDescription.text = forecast.weather.firstOrNull()?.description ?: ""

        // Icon
        val iconCode = forecast.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        holder.binding.imageViewWeatherIcon.load(iconUrl)

        // Animation
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(500).start()
    }

    override fun getItemCount() = forecastList.size

    fun updateData(newForecastList: List<ForecastItem>) {
        forecastList = newForecastList
        notifyDataSetChanged()
    }
}
