package com.example.weatherapp.viewmodel


import android.content.Context
import androidx.lifecycle.*
import com.example.weatherapp.data.api.WeatherService
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.WeatherDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val api: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    private val _forecast = MutableLiveData<List<ForecastItem>>()
    val forecast: LiveData<List<ForecastItem>> = _forecast



    fun fetchForecast(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getForecastByCity(city, Constants.API_KEY)
                // Lọc dữ liệu 1 mốc mỗi ngày (ví dụ 12:00:00)
                val daily = response.list.filter { it.dt_txt.contains("12:00:00") }
                _forecast.postValue(daily)
            } catch (e: Exception) {
                _forecast.postValue(emptyList())
            }
        }
    }


    fun fetchWeather(city: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getWeatherByCity(city, Constants.API_KEY)
                _weather.postValue(response)

                // ✅ Lưu cache
                WeatherDataStore.saveWeather(context, response)

            } catch (e: Exception) {
                _weather.postValue(null)
            }
        }
    }

    fun restoreWeather(cached: WeatherResponse) {
        _weather.value = cached
    }

}