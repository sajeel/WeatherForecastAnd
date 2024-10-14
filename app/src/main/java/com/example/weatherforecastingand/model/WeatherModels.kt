package com.example.weatherforecastingand.model


data class WeatherForecast(
    val city: String,
    val country: String,
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>
)

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String
)

data class HourlyForecast(
    val dateTime: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String
)