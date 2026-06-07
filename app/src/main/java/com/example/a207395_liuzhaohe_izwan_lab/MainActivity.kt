package com.example.a207395_liuzhaohe_izwan_lab

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a207395_liuzhaohe_izwan_lab.screen.ProfileScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.ReminderScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.StatsScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.WaterTrackerScreen
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import com.example.a207395_liuzhaohe_izwan_lab4.ui.CompareScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeDatabase
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeRepository
import com.example.compose.AppTheme
import com.example.a207395_liuzhaohe_izwan_lab.screen.WeatherScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.CommunityScreen
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    // 这个注解表示：该函数需要 Android O(API 26) 及以上版本
    // 因为你在项目中使用了 LocalDate（Java 8 时间API）
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ){ }
            .launch(
                Manifest.permission
                    .ACCESS_FINE_LOCATION
            )

        // 让界面可以绘制到状态栏区域（沉浸式UI）
        // false = 内容可以延伸到系统栏（更现代的UI设计）
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Jetpack Compose 的入口点
        // 所有 UI 都必须 write 在 setContent {} 内
        setContent {

            // 获取系统当前的深浅色状态
            val isDark = isSystemInDarkTheme()

            // 应用主题（颜色、字体、形状统一管理）
            AppTheme(
                darkTheme = isDark, // ✅ 绑定系统状态值，解决页面无法跟手跟随系统主题切换的问题
                dynamicColor = false
            ) {
                // NavController：导航控制器（核心对象）
                // 负责页面跳转、返回栈管理（类似传统 FragmentManager）
                val navController = androidx.navigation.compose.rememberNavController()
                // ViewModel：数据层（MVVM架构核心）
                // 生命周期安全（旋转屏幕不会丢数据）
                // 先获取 Context
                val context = LocalContext.current

                // 再创建数据库
                val database = remember {
                    HomeDatabase.getDatabase(context)
                }

                // 创建 Repository
                val repository = remember {
                    HomeRepository(
                        database.homeDao()
                    )
                }

                // 创建 ViewModel
                val viewModel: WaterViewModel =
                    viewModel(
                        factory =
                            object : androidx.lifecycle.ViewModelProvider.Factory {

                                override fun <T : ViewModel> create(
                                    modelClass: Class<T>
                                ): T {

                                    return WaterViewModel(
                                        repository
                                    ) as T
                                }
                            }
                    )

                // Scaffold：Material Design 页面结构容器
                // 提供：TopBar / BottomBar / FloatingButton 等布局槽位
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        // 自定义底部导航组件
                        // navController 传进去是为了让点击按钮可以触发页面跳转
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    // innerPadding 是 Scaffold 自动提供的“安全间距”
                    // 用来避免内容被 BottomBar 遮挡
                    // NavHost：导航容器（非常重要）
                    // 可以理解为“页面切换的容器”
                    androidx.navigation.compose.NavHost(

                        // 绑定导航控制器
                        navController = navController,

                        // 默认启动页面（App打开时显示）
                        startDestination = "home",

                        // 应用 Scaffold 的 padding，避免UI被挡住
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // composable(route) = 定义一个页面
                        // 当 route = "home" 时，这个页面会被显示
                        composable("home") {
                            WaterTrackerScreen(
                                viewModel,
                                navController
                            )
                        }
                        composable("stats") {
                            StatsScreen(viewModel)
                        }
                        // 当点击 BottomNavigationBar 里的 Compare 按钮
                        // navController.navigate("Compare") 会跳转到这里
                        composable("Compare") {
                            CompareScreen(viewModel)
                        }
                        composable("Reminder") {
                            ReminderScreen(viewModel)
                        }
                        // 目前只是一个简单 UI，没有逻辑
                        composable("Profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable("weather") {
                            WeatherScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable("community") {
                            CommunityScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "stats",
            onClick = {
                navController.navigate("stats") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.BarChart, null) },
            label = { Text("Stats") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "Compare",
            onClick = {
                navController.navigate("Compare") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.CompareArrows, null) },
            label = { Text("Compare") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "Reminder",
            onClick = {
                navController.navigate("Reminder") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Reminder") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "Profile",
            onClick = {
                navController.navigate("Profile") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
fun CardContentBox(title: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .width(230.dp) // 给定固定宽度，确保不会被压缩，从而产生滑动感
            .height(210.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 15.dp))
            content()
        }
    }
}
