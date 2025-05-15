package com.example.weatherapp.data.model

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: MainInfo,
    val weather: List<WeatherInfo>
)