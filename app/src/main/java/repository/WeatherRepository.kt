package com.example.a207395_liuzhaohe_izwan_lab.repository

import com.example.a207395_liuzhaohe_izwan_lab.network.RetrofitInstance

class WeatherRepository {
    suspend fun getWeather(
        latitude:Double,
        longitude:Double
    )=
        RetrofitInstance.api
            .getWeather(
                latitude,
                longitude
            )
}