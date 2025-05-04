package com.antand.clim8.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.antand.clim8.data.models.FavoriteCity

@Database(entities = [FavoriteCity::class], version = 1)
abstract class FavoriteCityDatabase : RoomDatabase() {

    abstract fun favoriteCityDao(): FavoriteCityDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteCityDatabase? = null

        fun getDatabase(context: Context): FavoriteCityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteCityDatabase::class.java,
                    "favorite_city_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
