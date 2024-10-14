package com.example.weatherforecastingand
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.ui.Modifier
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.weatherforecastingand.ui.WeatherApp
//import com.example.weatherforecastingand.ui.theme.WeatherForecastingAndTheme
//import com.example.weatherforecastingand.ViewModel.WeatherViewModel
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            WeatherForecastingAndTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val viewModel: WeatherViewModel = viewModel()
//                    WeatherApp(viewModel)
//                }
//            }
//        }
//    }
//}


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecastingand.NetworkLayer.WeatherApi
import com.example.weatherforecastingand.NetworkLayer.WeatherRepository
import com.example.weatherforecastingand.ui.WeatherApp
import com.example.weatherforecastingand.ui.theme.WeatherForecastingAndTheme
import com.example.weatherforecastingand.ViewModel.WeatherViewModel
import com.example.weatherforecastingand.ViewModel.WeatherViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val repository = WeatherRepository(weatherApi)
        val factory = WeatherViewModelFactory(repository)

        setContent {
            WeatherForecastingAndTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: WeatherViewModel = viewModel(factory = factory)
                    WeatherApp(viewModel)
                }
            }
        }
    }
}