package com.example.a207395_liuzhaohe_izwan_lab.model

data class WeatherResponse(
    val current: CurrentWeather,
    val daily: DailyWeather
)

data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int
)

data class DailyWeather(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)