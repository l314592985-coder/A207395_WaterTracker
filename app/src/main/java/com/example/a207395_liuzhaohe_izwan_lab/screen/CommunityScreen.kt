package com.example.a207395_liuzhaohe_izwan_lab.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import androidx.compose.foundation.lazy.items
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.CommunityViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(
    viewModel: WaterViewModel,
    navController: NavController
) {
    val homeList by viewModel.homeList.collectAsState()
    val context = LocalContext.current
    val communityViewModel: CommunityViewModel = viewModel()
    val communityList by communityViewModel
        .communityList
        .collectAsState()
    val selectedHomes = remember {
        mutableStateListOf<String>()
    }
    val snackbarHostState =
        remember { SnackbarHostState() }
    val scope =
        rememberCoroutineScope()
    LaunchedEffect(Unit){
        communityViewModel.loadCommunity()
    }
    val selectedCount = selectedHomes.size

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
                text = "Community Sharing",
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

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
                    containerColor =
                        MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sharing Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Selected Homes: $selectedCount"
                    )
                    Text(
                        text = "Total Homes: ${homeList.size}"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = {
                    homeList.forEach { home ->
                        if(
                            selectedHomes.contains(
                                home.home_name
                            )
                        ){
                            viewModel.uploadCommunityPost(
                                home.home_name,
                                home.current,
                                home.target
                            )
                        }
                    }
                    val shareText =
                        buildString {
                            append(
                                "My Water Tracker Achievement\n\n"
                            )
                            homeList.forEach { home ->
                                if(
                                    selectedHomes.contains(
                                        home.home_name
                                    )
                                ){
                                    val percentage =
                                        if(home.target > 0)
                                            ((home.current / home.target) * 100).toInt()
                                        else
                                            0
                                    append(
                                        "${home.home_name}\n"
                                    )
                                    append(
                                        "Current: ${home.current} L\n"
                                    )
                                    append(
                                        "Target: ${home.target} L\n"
                                    )
                                    append(
                                        "Progress: $percentage%\n\n"
                                    )
                                }
                            }
                        }

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Successfully uploaded to Community"
                        )
                    }

                    val intent =
                        Intent(
                            Intent.ACTION_SEND
                        )
                    intent.type = "text/plain"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        shareText
                    )
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            "Share Achievement"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    "Share Selected Homes"
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                item {

                    Text(
                        text = "Select Homes",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                clip = false
                            ),

                        shape = RoundedCornerShape(20.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        ),

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 12.dp,
                                end = 12.dp,
                                top = 12.dp,
                                bottom = 20.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            items(homeList.size) { index ->

                                val home =
                                    homeList[index]

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(15.dp),
                                            clip = false
                                        ),

                                    shape = RoundedCornerShape(15.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.surface
                                    ),

                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 0.dp
                                    )
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = home.home_name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )

                                            Checkbox(
                                                checked =
                                                    selectedHomes.contains(
                                                        home.home_name
                                                    ),

                                                onCheckedChange = {

                                                    if(it){
                                                        selectedHomes.add(
                                                            home.home_name
                                                        )

                                                    }else{
                                                        selectedHomes.remove(
                                                            home.home_name
                                                        )
                                                    }
                                                }
                                            )
                                        }

                                        Spacer(
                                            modifier = Modifier.height(15.dp)
                                        )

                                        Text(
                                            text = "${home.current} L",
                                            fontSize = 14.sp
                                        )

                                        Text(
                                            text = "/ ${home.target} L",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(
                                            modifier = Modifier.height(8.dp)
                                        )

                                        LinearProgressIndicator(
                                            progress = {
                                                if(home.target > 0)
                                                    (home.current / home.target)
                                                        .toFloat()
                                                        .coerceIn(0f,1f)
                                                else
                                                    0f
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = "Recent Uploads",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }

                items(communityList) { item ->

                    val percentage =
                        if (item.target > 0)
                            ((item.current / item.target) * 100)
                                .toInt()
                        else
                            0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                clip = false
                            ),

                        shape = RoundedCornerShape(20.dp),

                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        ),

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                item.homeName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                "Current: ${item.current} L"
                            )

                            Text(
                                "Target: ${item.target} L"
                            )

                            Text(
                                "Progress: $percentage%"
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        )
    }
}