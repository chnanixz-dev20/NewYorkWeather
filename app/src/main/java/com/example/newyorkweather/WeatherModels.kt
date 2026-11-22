package com.example.newyorkweather

data class WeatherResponse(
    val name: String,
    val weather: List<Weather>
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)