package com.example.weatherforecastingand

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherforecastingand.NetworkLayer.WeatherRepository
import com.example.weatherforecastingand.ViewModel.WeatherViewModel
import com.example.weatherforecastingand.model.CurrentWeather
import com.example.weatherforecastingand.model.HourlyForecast
import com.example.weatherforecastingand.model.WeatherForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var repository: WeatherRepository

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `fetchWeatherForecast updates weatherForecast state on success`() = runBlockingTest {
        // Given
        val mockForecast = WeatherForecast(
            city = "Dubai",
            country = "AE",
            currentWeather = CurrentWeather(30.0, 32.0, 50, 5.0, "Clear sky", "01d"),
            hourlyForecast = listOf(
                HourlyForecast("2024-10-14 15:00:00", 30.0, 32.0, 50, 5.0, "Clear sky", "01d")
            )
        )
        `when`(repository.getWeatherForecast("Dubai")).thenReturn(mockForecast)

        // When
        viewModel.fetchWeatherForecast()

        // Then
        assert(viewModel.weatherForecast.first() == mockForecast)
        assert(viewModel.error.first() == null)
    }

    @Test
    fun `fetchWeatherForecast updates error state on network error`() = runBlockingTest {
        // Given
        `when`(repository.getWeatherForecast("Dubai")).thenThrow(IOException("Network error"))

        // When
        viewModel.fetchWeatherForecast()

        // Then
        assert(viewModel.weatherForecast.first() == null)
        assert(viewModel.error.first() == "Network error occurred. Please check your connection.")
    }

    @Test
    fun `fetchWeatherForecast updates error state on API error`() = runBlockingTest {
        // Given
        val mockResponse = Response.error<WeatherForecast>(404, okhttp3.ResponseBody.create(null, ""))
        `when`(repository.getWeatherForecast("Dubai")).thenThrow(HttpException(mockResponse))

        // When
        viewModel.fetchWeatherForecast()

        // Then
        assert(viewModel.weatherForecast.first() == null)
        assert(viewModel.error.first() == "API error occurred. Please try again later.")
    }

    @Test
    fun `setCity with empty string doesn't update city state`() = runBlockingTest {
        // When
        viewModel.setCity("")

        // Then
        assert(viewModel.city.first() != "")
        assert(viewModel.error.first() == "City name cannot be empty.")
    }

    @Test
    fun `setCity with valid string updates city state and clears error`() = runBlockingTest {
        // Given
        viewModel.setCity("") // Set an error first

        // When
        viewModel.setCity("Dubai")

        // Then
        assert(viewModel.city.first() == "Dubai")
        assert(viewModel.error.first() == null)
    }
}