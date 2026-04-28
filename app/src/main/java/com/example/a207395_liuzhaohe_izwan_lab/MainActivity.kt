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
import com.example.compose.backgroundDark
import com.example.compose.backgroundLight
import com.example.compose.onBackgroundDark
import com.example.compose.onBackgroundLight
import com.example.compose.onPrimaryDark
import com.example.compose.onPrimaryLight
import com.example.compose.onSecondaryDark
import com.example.compose.onSecondaryLight
import com.example.compose.onSurfaceDark
import com.example.compose.onSurfaceLight
import com.example.compose.primaryDark
import com.example.compose.primaryLight
import com.example.compose.secondaryDark
import com.example.compose.secondaryLight
import com.example.compose.surfaceDark
import com.example.compose.surfaceLight
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.a207395_liuzhaohe_izwan_lab.screen.ProfileScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.ReminderScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.StatsScreen
import com.example.a207395_liuzhaohe_izwan_lab.screen.WaterTrackerScreen
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import com.example.a207395_liuzhaohe_izwan_lab4.ui.CompareScreen

private val LightColors = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,

    secondary = secondaryLight,
    onSecondary = onSecondaryLight,

    background = backgroundLight,
    onBackground = onBackgroundLight,

    surface = surfaceLight,
    onSurface = onSurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,

    secondary = secondaryDark,
    onSecondary = onSecondaryDark,

    background = backgroundDark,
    onBackground = onBackgroundDark,

    surface = surfaceDark,
    onSurface = onSurfaceDark
)

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    // 使用 MaterialTheme 统一管理颜色、字体、组件风格
    // 所有 UI 组件都会继承这个 Theme
    val colors = if (darkTheme) DarkColors else LightColors
    // 根据系统深浅模式切换颜色

    MaterialTheme(
        //控制全局颜色 更改可以从color.kt或theme.kt修改 primary是用户放更多注意力的地方 surface是容器
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}

class MainActivity : ComponentActivity() {
    // 这个注解表示：该函数需要 Android O(API 26) 及以上版本
    // 因为你在项目中使用了 LocalDate（Java 8 时间API）
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 让界面可以绘制到状态栏区域（沉浸式UI）
        // false = 内容可以延伸到系统栏（更现代的UI设计）
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Jetpack Compose 的入口点
        // 所有 UI 都必须写在 setContent {} 内
        setContent {

            // 应用主题（颜色、字体、形状统一管理）
            MyAppTheme {
                // NavController：导航控制器（核心对象）
                // 负责页面跳转、返回栈管理（类似传统 FragmentManager）
                val navController = androidx.navigation.compose.rememberNavController()
                // ViewModel：数据层（MVVM架构核心）
                // 生命周期安全（旋转屏幕不会丢数据）
                val viewModel: WaterViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()

                // Scaffold：Material Design 页面结构容器
                // 提供：TopBar / BottomBar / FloatingButton 等布局槽位
                Scaffold(
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
                            WaterTrackerScreen(viewModel)
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
                            ProfileScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

// 修改后的 BottomNavigationBar
@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "stats",
            onClick = { navController.navigate("stats") { launchSingleTop = true } },
            icon = { Icon(Icons.Default.BarChart, null) },
            label = { Text("Stats") }
        )

        // 在 BottomNavigationBar 内部
        NavigationBarItem(
            selected = currentRoute == "Compare", // 如果当前路径是 Compare，图标变亮
            onClick = { navController.navigate("Compare") { launchSingleTop = true } },
            icon = { Icon(Icons.Default.CompareArrows, null) }, // 使用对比图标
            label = { Text("Compare") }
        )

        NavigationBarItem(
            selected = currentRoute == "Reminder",
            onClick = { navController.navigate("Reminder") { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Reminder") }
        )

        NavigationBarItem(
            selected = currentRoute == "Profile",
            onClick = { navController.navigate("Profile") { launchSingleTop = true } },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") }
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