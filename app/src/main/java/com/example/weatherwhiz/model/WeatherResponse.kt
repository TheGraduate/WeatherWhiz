package com.example.weatherwhiz.model

data class WeatherResponse(
    val main: Main,
    val name: String
)

data class Main(
    val temp: Double
)