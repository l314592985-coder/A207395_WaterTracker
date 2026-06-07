package com.example.a207395_liuzhaohe_izwan_lab.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(
                "https://api.open-meteo.com/"
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                WeatherApi::class.java
            )
    }
}