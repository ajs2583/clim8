package com.antand.clim8.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.antand.clim8.data.models.FavoriteCity

@Dao
interface FavoriteCityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: FavoriteCity)

    @Delete
    suspend fun delete(city: FavoriteCity)

    @Query("SELECT * FROM favorite_cities ORDER BY timestamp DESC")
    fun getAllFavorites(): LiveData<List<FavoriteCity>>

    @Query("SELECT COUNT(*) FROM favorite_cities WHERE cityName = :cityName")
    suspend fun cityExists(cityName: String): Int

}
