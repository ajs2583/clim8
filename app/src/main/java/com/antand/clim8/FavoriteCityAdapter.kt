package com.antand.clim8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antand.clim8.databinding.ItemFavoriteCityBinding

class FavoriteCityAdapter(
    private var favorites: List<FavoriteCity>,
    private val onCityClicked: (String) -> Unit,
    private val onDeleteClicked: (FavoriteCity) -> Unit
) : RecyclerView.Adapter<FavoriteCityAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemFavoriteCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val cityName = favorites[adapterPosition].cityName
                onCityClicked(cityName)
            }
            binding.buttonDeleteCity.setOnClickListener {
                val city = favorites[adapterPosition]
                onDeleteClicked(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val city = favorites[position]
        holder.binding.textCityName.text = city.cityName
    }

    override fun getItemCount() = favorites.size

    fun updateData(newFavorites: List<FavoriteCity>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }
}
