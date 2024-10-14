package com.example.weatherforecastingand.NetworkLayer

import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.weatherforecastingand.model.WeatherForecast
import com.example.weatherforecastingand.model.CurrentWeather
import com.example.weatherforecastingand.model.HourlyForecast
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException

import com.example.weatherforecastingand.model.WeatherResponse
import retrofit2.Response

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}



class WeatherRepository(private val api: WeatherApi) {

    private val cache = mutableMapOf<String, CacheEntry<WeatherForecast>>()
    private val mutex = Mutex()

    suspend fun getWeatherForecast(city: String): WeatherForecast {
        mutex.withLock {
            val cachedData = cache[city]
            if (cachedData != null && !cachedData.isExpired()) {
                return cachedData.data
            }
        }

        try {
            val response = api.getWeatherForecast(city, API_KEY)
            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body()!!
                val weatherForecast = weatherResponse.toWeatherForecast()
                mutex.withLock {
                    cache[city] = CacheEntry(weatherForecast)
                }
                return weatherForecast
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw IOException("Network error occurred while fetching weather forecast", e)
        } catch (e: HttpException) {
            throw HttpException(e.response())
        } catch (e: Exception) {
            throw IllegalStateException("An unexpected error occurred while fetching weather forecast", e)
        }
    }

    private fun com.example.weatherforecastingand.model.WeatherResponse.toWeatherForecast(): WeatherForecast {
        val currentWeather = this.list.first().let { item ->
            CurrentWeather(
                temperature = item.main.temp,
                feelsLike = item.main.feelsLike,
                humidity = item.main.humidity,
                windSpeed = item.wind.speed,
                description = item.weather.first().description,
                icon = item.weather.first().icon
            )
        }

        val hourlyForecast = this.list.map { item ->
            HourlyForecast(
                dateTime = item.dtTxt,
                temperature = item.main.temp,
                feelsLike = item.main.feelsLike,
                humidity = item.main.humidity,
                windSpeed = item.wind.speed,
                description = item.weather.first().description,
                icon = item.weather.first().icon
            )
        }

        return WeatherForecast(
            city = this.city.name,
            country = this.city.country,
            currentWeather = currentWeather,
            hourlyForecast = hourlyForecast
        )
    }

    companion object {
        const val API_KEY = "bd5e378503939ddaee76f12ad7a97608"
        private const val CACHE_DURATION_MINUTES = 15L
    }

    private class CacheEntry<T>(val data: T, val timestamp: Long = System.currentTimeMillis()) {
        fun isExpired(): Boolean {
            val currentTime = System.currentTimeMillis()
            return (currentTime - timestamp) > TimeUnit.MINUTES.toMillis(CACHE_DURATION_MINUTES)
        }
    }
}




