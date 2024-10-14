
# Weather Forecasting Android App

## Overview

This Weather Forecasting Android app provides users with current weather information and a 5-day forecast for any city. Built with Kotlin and Jetpack Compose, it demonstrates modern Android development practices including MVVM architecture, Retrofit for network requests, and Jetpack Navigation.

## Features

- Search for weather information by city name
- Display current weather conditions including temperature, humidity, wind speed, and weather description
- Show a 5-day weather forecast with hourly breakdowns
- Clean and intuitive user interface built with Jetpack Compose
- Caching mechanism to reduce API calls and improve app performance

## Tech Stack

- Kotlin
- Jetpack Compose for UI
- Retrofit for API calls
- Coroutines for asynchronous programming
- ViewModel for managing UI-related data
- Navigation Component for handling navigation between screens
- MVVM (Model-View-ViewModel) architecture

## Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/WeatherForecastingAnd.git
   ```

2. Open the project in Android Studio.

3. In the `WeatherRepository.kt` file, replace `"your_api_key_here"` with your actual OpenWeatherMap API key.

4. Build and run the project on an emulator or physical device.

## Project Structure

- `data/`: Contains the `WeatherApi` interface and `WeatherRepository` class for handling data operations.
- `model/`: Defines data classes for weather information.
- `ui/`: Contains Composable functions for the app's user interface.
- `viewmodel/`: Houses the `WeatherViewModel` for managing UI state and business logic.
- `MainActivity.kt`: The entry point of the application.

## How to Use

1. Launch the app.
2. Enter a city name in the search field.
3. Tap the search button to fetch current weather data.
4. View the current weather information displayed on the main screen.
5. Tap the "Five Day Forecast" button to see the detailed 5-day forecast.
6. Use the "Back to Current Weather" button to return to the main screen.

## API

This app uses the OpenWeatherMap API to fetch weather data. You'll need to sign up for a free API key at [OpenWeatherMap](https://openweathermap.org/api) and add it to the project as described in the Setup section.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).
 