package com.example.weatherforecastingand
import com.example.weatherforecastingand.NetworkLayer.WeatherRepository
import com.example.weatherforecastingand.NetworkLayer.WeatherApi
import com.example.weatherforecastingand.model.City
import com.example.weatherforecastingand.model.Clouds
import com.example.weatherforecastingand.model.WeatherResponse
import com.example.weatherforecastingand.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    @Mock
    private lateinit var api: WeatherApi

    private lateinit var repository: WeatherRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = WeatherRepository(api)
    }

    @Test
    fun `getWeatherForecast returns expected data on success`() = runBlockingTest {
        // Given
        val mockResponse = WeatherResponse(
            cod = "200",
            message = 0,
            cnt = 1,
            list = listOf(
                ForecastItem(
                    dt = 1728918000,
                    main = MainWeatherData(
                        temp = 30.0,
                        feelsLike = 32.0,
                        tempMin = 28.0,
                        tempMax = 32.0,
                        pressure = 1008,
                        seaLevel = 1008,
                        grndLevel = 1007,
                        humidity = 50,
                        tempKf = 0.0
                    ),
                    weather = listOf(
                        WeatherDescription(
                            id = 800,
                            main = "Clear",
                            description = "clear sky",
                            icon = "01d"
                        )
                    ),
                    clouds = Clouds(0),
                    wind = Wind(speed = 5.0, deg = 180, gust = 7.0),
                    visibility = 10000,
                    pop = 0.0,
                    sys = Sys("d"),
                    dtTxt = "2024-10-14 15:00:00"
                )
            ),
            city = City(
                id = 292223,
                name = "Dubai",
                coord = Coord(lat = 25.2582, lon = 55.3047),
                country = "AE",
                population = 1137347,
                timezone = 14400,
                sunrise = 1728785764,
                sunset = 1728827614
            )
        )
        `when`(api.getWeatherForecast("Dubai", WeatherRepository.API_KEY)).thenReturn(Response.success(mockResponse))

        // When
        val result = repository.getWeatherForecast("Dubai")

        // Then
        assert(result.city == "Dubai")
        assert(result.country == "AE")
        assert(result.currentWeather.temperature == 30.0)
        assert(result.hourlyForecast.size == 1)
    }

    @Test(expected = IOException::class)
    fun `getWeatherForecast throws IOException on network error`() = runBlockingTest {
        // Given
        `when`(api.getWeatherForecast("Dubai", WeatherRepository.API_KEY)).thenThrow(IOException("Network error"))

        // When
        repository.getWeatherForecast("Dubai")

        // Then: expect IOException to be thrown
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `getWeatherForecast throws HttpException on API error`() = runBlockingTest {
        // Given
        val errorResponse = Response.error<WeatherResponse>(404, okhttp3.ResponseBody.create(null, ""))
        `when`(api.getWeatherForecast("NonexistentCity", WeatherRepository.API_KEY)).thenReturn(errorResponse)

        // When
        repository.getWeatherForecast("NonexistentCity")

        // Then: expect HttpException to be thrown
    }
}