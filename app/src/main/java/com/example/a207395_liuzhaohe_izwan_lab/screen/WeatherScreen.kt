package com.example.a207395_liuzhaohe_izwan_lab.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.a207395_liuzhaohe_izwan_lab.location.LocationHelper
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WeatherViewModel
import android.location.Geocoder
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import java.util.Locale
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.sensor.StepCounterHelper
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun WeatherScreen(
    viewModel: WaterViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val weatherViewModel: WeatherViewModel = viewModel()
    val temperature by weatherViewModel
        .temperature
        .collectAsState()
    val humidity by weatherViewModel
        .humidity
        .collectAsState()
    val steps by
    weatherViewModel.steps.collectAsState()
    val latitude by
    weatherViewModel
        .latitude
        .collectAsState()
    val longitude by
    weatherViewModel
        .longitude
        .collectAsState()
    val cityName by
    weatherViewModel
        .cityName
        .collectAsState()
    val weight =
        viewModel.userWeight

    DisposableEffect(Unit){

        val helper =
            StepCounterHelper(context){

                weatherViewModel.updateSteps(it)
            }

        helper.start()

        onDispose {
            helper.stop()
        }
    }

    LaunchedEffect(Unit) {
        LocationHelper(context)
            .getCurrentLocation { lat, long ->

                weatherViewModel
                    .updateLocation(
                        lat,
                        long
                    )

                weatherViewModel
                    .loadWeather(
                        lat,
                        long
                    )

                try {
                    val geocoder =
                        Geocoder(
                            context,
                            Locale.getDefault()
                        )
                    val addresses =
                        geocoder.getFromLocation(
                            lat,
                            long,
                            1
                        )
                    val city =
                        addresses
                            ?.firstOrNull()
                            ?.locality
                    if (city != null) {

                        weatherViewModel.updateCity(city)

                        viewModel.updateCity(city)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.1f
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .statusBarsPadding()
                .padding(
                    start = 25.dp,
                    end = 25.dp,
                    top = 5.dp,
                    bottom = 25.dp
                )
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Weather & Location",
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ================= Recommendation Card =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Water Intake Recommendation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    val temp =
                        temperature.toDoubleOrNull() ?: 0.0
                    val distance =
                        steps * 0.0007
                    val baseWater =
                        weight * 35 / 1000
                    val exerciseWater =
                        distance * 0.25
                    val tempBonus =
                        when{
                            temp >= 35 -> 1.0
                            temp >= 30 -> 0.5
                            else -> 0.0
                        }
                    val recommendedWater =
                        baseWater + exerciseWater + tempBonus

                    Text(
                        text =
                            "Weight: ${weight.toInt()} kg\n" +
                                    "Temperature: ${temp.toInt()}°C\n" +
                                    "Distance: ${String.format("%.2f",distance)} km\n" +
                                    "Recommended Intake: ${String.format("%.2f",recommendedWater)} L"
                    )

                    if(distance >= 5){

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "⚠ Activity level is high. Please drink water soon.",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ================= Location Card =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(cityName)

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text("Latitude: $latitude")
                    Text("Longitude: $longitude")
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ================= Weather Card =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Current Weather",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Temperature: $temperature°C"
                    )

                    Text(
                        text = "Humidity: $humidity%"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ================= Distance Card =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        "Today's Activity",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        "Steps: $steps"
                    )

                    Text(
                        "Distance: ${
                            String.format(
                                "%.2f",
                                steps * 0.0007
                            )
                        } km"
                    )
                }
            }
        }
    }
}