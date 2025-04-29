package com.antand.clim8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antand.clim8.databinding.ForecastCardBinding
import coil.load
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private var forecastList: List<DailyForecast>) :
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

        val date = Date(forecast.dt * 1000)
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        holder.binding.textViewDate.text = dateFormat.format(date)

        val isCelsius = SettingsManager.isUsingCelsius(context)
        val highTemp = if (isCelsius) forecast.temp.max.toInt() else TemperatureUtils.celsiusToFahrenheit(forecast.temp.max)
        val lowTemp = if (isCelsius) forecast.temp.min.toInt() else TemperatureUtils.celsiusToFahrenheit(forecast.temp.min)

        holder.binding.textViewTemp.text = "High: $highTemp° / Low: $lowTemp°"
        holder.binding.textViewDescription.text = forecast.weather.firstOrNull()?.description ?: ""

        val iconCode = forecast.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
        holder.binding.imageViewWeatherIcon.load(iconUrl)

        holder.itemView.alpha = 0f
        holder.itemView.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    override fun getItemCount() = forecastList.size

    fun updateData(newForecastList: List<DailyForecast>) {
        forecastList = newForecastList
        notifyDataSetChanged()
    }

    fun updateDataFromFiveDay(newList: List<ForecastItem>) {
        forecastList = newList.map { daily ->
            DailyForecast(
                dt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(daily.dt_txt)?.time?.div(1000) ?: 0L,
                temp = Temp(
                    day = daily.main.temp,
                    min = daily.main.temp_min,
                    max = daily.main.temp_max
                ),
                weather = daily.weather
            )
        }
        notifyDataSetChanged()
    }
}

