package com.example.weatherforecastingand.ViewModel
//import androidx.lifecycle.ViewModel
// import androidx.lifecycle.viewModelScope
//import com.example.weatherforecastingand.NetworkLayer.WeatherRepository
//import com.example.weatherforecastingand.model.WeatherForecast
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import retrofit2.HttpException
//import java.io.IOException
//
//class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
//
//    private val _weatherForecast = MutableStateFlow<WeatherForecast?>(null)
//    val weatherForecast: StateFlow<WeatherForecast?> = _weatherForecast
//
//    private val _city = MutableStateFlow("Dubai") // Default city
//    val city: StateFlow<String> = _city
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    init {
//        fetchWeatherForecast()
//    }
//
//    fun setCity(newCity: String) {
//        if (newCity.isBlank()) {
//            _error.value = "City name cannot be empty."
//            return
//        }
//        _city.value = newCity
//        _error.value = null
//        fetchWeatherForecast()
//    }
//
//    fun fetchWeatherForecast() {
//        viewModelScope.launch {
//            try {
//                _weatherForecast.value = repository.getWeatherForecast(_city.value)
//                _error.value = null
//            } catch (e: Exception) {
//                _weatherForecast.value = null
//                _error.value = when (e) {
//                    is IOException -> "Network error occurred. Please check your connection."
//                    is HttpException -> "API error occurred. Please try again later."
//                    else -> "An unexpected error occurred while fetching weather forecast."
//                }
//            }
//        }
//    }
//}


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastingand.NetworkLayer.WeatherRepository
import com.example.weatherforecastingand.model.WeatherForecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherForecast = MutableStateFlow<WeatherForecast?>(null)
    val weatherForecast: StateFlow<WeatherForecast?> = _weatherForecast

    private val _city = MutableStateFlow("Dubai") // Default city
    val city: StateFlow<String> = _city

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchWeatherForecast()
    }

    fun setCity(newCity: String) {
        if (newCity.isBlank()) {
            _error.value = "City name cannot be empty."
            return
        }
        _city.value = newCity
        _error.value = null
        fetchWeatherForecast()
    }

    fun fetchWeatherForecast() {
        viewModelScope.launch {
            try {
                _weatherForecast.value = repository.getWeatherForecast(_city.value)
                _error.value = null
            } catch (e: Exception) {
                _weatherForecast.value = null
                _error.value = when (e) {
                    is IOException -> "Network error occurred. Please check your connection."
                    is HttpException -> "API error occurred. Please try again later."
                    else -> "An unexpected error occurred while fetching weather forecast."
                }
            }
        }
    }
}