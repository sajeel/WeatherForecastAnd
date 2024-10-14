package com.example.weatherforecastingand.DataLayer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "forecast_cache")
data class ForecastCache(
    @PrimaryKey val city: String,
    val forecastData: String,
    val timestamp: Long
)

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast_cache WHERE city = :city")
    fun getForecastForCity(city: String): Flow<ForecastCache?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecastCache: ForecastCache)

    @Query("DELETE FROM forecast_cache WHERE city = :city")
    suspend fun deleteForecastForCity(city: String)
}

@Database(entities = [ForecastCache::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: android.content.Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}