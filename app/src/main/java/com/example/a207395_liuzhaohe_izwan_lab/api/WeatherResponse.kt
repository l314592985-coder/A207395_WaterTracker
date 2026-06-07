package com.example.a207395_liuzhaohe_izwan_lab.api

data class WeatherResponse(

    val current: CurrentWeather
)

data class CurrentWeather(

    val temperature_2m: Double,

    val relative_humidity_2m: Int
)