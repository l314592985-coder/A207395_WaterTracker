package com.example.a207395_liuzhaohe_izwan_lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a207395_liuzhaohe_izwan_lab.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository =
        WeatherRepository()

    private val _temperature =
        MutableStateFlow("--")

    val temperature: StateFlow<String>
            = _temperature

    private val _humidity =
        MutableStateFlow("--")

    val humidity: StateFlow<String>
            = _humidity

    private val _latitude =
        MutableStateFlow("--")

    val latitude: StateFlow<String>
            = _latitude

    private val _longitude =
        MutableStateFlow("--")

    val longitude: StateFlow<String>
            = _longitude

    private val _cityName =
        MutableStateFlow("Loading...")

    val cityName: StateFlow<String>
            = _cityName

    fun updateCity(
        city:String
    ){
        _cityName.value = city
    }

    fun updateLocation(
        lat:Double,
        lon:Double
    ){
        _latitude.value =
            String.format("%.4f",lat)

        _longitude.value =
            String.format("%.4f",lon)
    }

    fun loadWeather(
        latitude:Double,
        longitude:Double
    ){
        viewModelScope.launch {

            try{

                val result =
                    repository.getWeather(
                        latitude,
                        longitude
                    )

                _temperature.value =
                    result.current.temperature_2m
                        .toString()

                _humidity.value =
                    result.current.relative_humidity_2m
                        .toString()

            }catch(e:Exception){

                e.printStackTrace()
            }
        }
    }

    private val _steps =
        MutableStateFlow(0)

    val steps: StateFlow<Int>
            = _steps

    fun updateSteps(
        value:Int
    ){
        _steps.value = value
    }

    val distanceKm: Double
        get() = steps.value * 0.0007
}