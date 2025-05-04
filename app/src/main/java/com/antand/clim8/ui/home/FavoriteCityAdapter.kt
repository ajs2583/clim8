package com.antand.clim8.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antand.clim8.data.models.FavoriteCity
import com.antand.clim8.databinding.ItemFavoriteCityBinding

// adapter class for a RecyclerView displaying a list of favorite cities
class FavoriteCityAdapter(
    // list of favorite cities to display
    private var favorites: List<FavoriteCity>,

    // lambda to handle clicking a city
    private val onCityClicked: (String) -> Unit,

    // lambda to handle deleting a city
    private val onDeleteClicked: (FavoriteCity) -> Unit
) : RecyclerView.Adapter<FavoriteCityAdapter.FavoriteViewHolder>() {

    // ViewHolder inner class holds the view for each item in the list
    inner class FavoriteViewHolder(val binding: ItemFavoriteCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // initialization block to set click listeners for the item view and delete button
        init {
            // when the root view is clicked, pass the city name to the click handler
            binding.root.setOnClickListener {
                val cityName = favorites[adapterPosition].cityName
                onCityClicked(cityName)
            }
            // when the delete button is clicked, pass the full city object to the delete handler
            binding.buttonDeleteCity.setOnClickListener {
                val city = favorites[adapterPosition]
                onDeleteClicked(city)
            }
        }
    }

    // called when the ViewHolder is created; inflates the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    // binds data to each ViewHolder based on its position in the list
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val city = favorites[position]
        holder.binding.textCityName.text = city.cityName // Set city name in the TextView
    }

    // returns the number of items in the list
    override fun getItemCount() = favorites.size

    // updates the list of favorite cities and refreshes the RecyclerView
    fun updateData(newFavorites: List<FavoriteCity>) {
        favorites = newFavorites
        notifyDataSetChanged() // Notifies RecyclerView that data has changed
    }
}
