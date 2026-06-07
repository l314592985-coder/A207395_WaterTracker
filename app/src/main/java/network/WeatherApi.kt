package com.example.a207395_liuzhaohe_izwan_lab.network

import com.example.a207395_liuzhaohe_izwan_lab.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")

    suspend fun getWeather(

        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m",

        @Query("daily")
        daily: String =
            "temperature_2m_max,temperature_2m_min",

        @Query("forecast_days")
        days: Int = 3

    ): WeatherResponse
}