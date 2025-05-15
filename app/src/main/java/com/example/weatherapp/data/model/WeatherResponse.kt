package com.example.weatherapp.data.model

data class WeatherResponse(
    val name: String,              // Tên thành phố
    val main: MainInfo,            // Nhiệt độ
    val weather: List<WeatherInfo> // Thời tiết (dạng mảng)
)

data class MainInfo(
    val temp: Double
)

data class WeatherInfo(
    val description: String,
    val icon: String
)