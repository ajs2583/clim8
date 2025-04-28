package com.antand.clim8

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val timestamp: Long = System.currentTimeMillis()
)
