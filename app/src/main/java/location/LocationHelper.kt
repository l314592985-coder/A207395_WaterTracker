package com.example.a207395_liuzhaohe_izwan_lab.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

class LocationHelper(
    private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onResult:(Double,Double)->Unit
    ){
        val client =
            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        client.lastLocation
            .addOnSuccessListener {
                if(it != null){
                    onResult(
                        it.latitude,
                        it.longitude
                    )

                }
            }
    }
}