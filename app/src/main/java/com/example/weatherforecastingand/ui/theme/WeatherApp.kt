package com.example.weatherforecastingand.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherforecastingand.model.CurrentWeather
import com.example.weatherforecastingand.model.HourlyForecast
 import com.example.weatherforecastingand.ViewModel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(viewModel = viewModel, onNavigateToForecast = {
                navController.navigate("forecast")
            })
        }
        composable("forecast") {
            ForecastScreen(viewModel = viewModel, onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}

@Composable
fun MainScreen(viewModel: WeatherViewModel, onNavigateToForecast: () -> Unit) {
    val weatherForecast by viewModel.weatherForecast.collectAsState()
    val city by viewModel.city.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CityInput(
            city = city,
            onCityChange = { viewModel.setCity(it) },
            onSearch = { viewModel.fetchWeatherForecast() }
        )

        when {
            error != null -> ErrorMessage(error = error!!)
            weatherForecast != null -> {
                CurrentWeatherCard(weatherForecast!!.currentWeather, weatherForecast!!.city, weatherForecast!!.country)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToForecast,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Five Day Forecast")
                }
            }
            else -> LoadingIndicator()
        }
    }
}

@Composable
fun ForecastScreen(viewModel: WeatherViewModel, onNavigateBack: () -> Unit) {
    val weatherForecast by viewModel.weatherForecast.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Five Day Forecast",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (weatherForecast != null) {
            LazyColumn {
                items(weatherForecast!!.hourlyForecast.groupBy { it.dateTime.substring(0, 10) }.values.take(5)) { dailyForecasts ->
                    DailyForecastItem(dailyForecasts)
                }
            }
        } else {
            Text("No forecast data available")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to Current Weather")
        }
    }
}

@Composable
fun CityInput(
    city: String,
    onCityChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("Enter city") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSearch) {
            Text("Search")
        }
    }
}

@Composable
fun ErrorMessage(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CurrentWeatherCard(currentWeather: CurrentWeather, city: String, country: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$city, $country",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${currentWeather.temperature}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Feels like: ${currentWeather.feelsLike}°C")
            Text(text = "Humidity: ${currentWeather.humidity}%")
            Text(text = "Wind Speed: ${currentWeather.windSpeed} m/s")
            Text(
                text = currentWeather.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DailyForecastItem(dailyForecasts: List<HourlyForecast>) {
    val date = dailyForecasts.first().dateTime.substring(0, 10)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = formatDate(date),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            dailyForecasts.forEach { forecast ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(forecast.dateTime),
                        modifier = Modifier.width(48.dp)
                    )
                    Text(
                        text = "${forecast.temperature}°C",
                        modifier = Modifier.width(64.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = forecast.description,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date!!)
}

fun formatTime(dateTime: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = inputFormat.parse(dateTime)
    return outputFormat.format(date!!)
}