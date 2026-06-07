package com.example.a207395_liuzhaohe_izwan_lab.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")

    suspend fun getWeather(

        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m"

    ): WeatherResponse
}