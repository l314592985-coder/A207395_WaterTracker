package com.example.a207395_liuzhaohe_izwan_lab1

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.*
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView)
            ?.isAppearanceLightStatusBars = false

        setContent {
            MyAppTheme {   // 用导出的 Theme
                WaterTrackerScreen("Water Tracker")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterTrackerScreen(title: String) {
    val homes = rememberSaveable { mutableStateListOf(0.0, 0.0, 0.0) }
    var selectedHome by remember { mutableStateOf(0) }
    var showInputDialog by remember { mutableStateOf(false) }
    //ui自动更新 因为用了state driven状态驱动式重组
    var inputAmount by remember { mutableStateOf("") }
    val homeNames = rememberSaveable { mutableStateListOf("Home1", "Home2", "Home3") }
    var showAddHomeDialog by remember { mutableStateOf(false) }
    var newHomeName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteIndex by remember { mutableStateOf(-1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val history = rememberSaveable {
        mutableStateListOf(
            mutableStateListOf<Double>(),
            mutableStateListOf<Double>(),
            mutableStateListOf<Double>()
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        bottomBar = {
            BottomNavigationBar()   // navigation bar
        }

    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(R.drawable.wallpaper),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.1F
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .padding(horizontal = 25.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // 标题 + 头像
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Image(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Home 列表
                homes.forEachIndexed { index, value ->

                    HomeProgressCard(
                        homeName = homeNames[index],
                        current = value,
                        target = 200.0,
                        onAdd = {
                            selectedHome = index
                            showInputDialog = true
                            //点击add后 状态修改为true if语句成立并执行
                            //如果要取消弹出式窗口 删除此行 删除text=和confirmbotton=
                        },
                        onUndo = {
                            if (history[index].isNotEmpty()) {
                                val undoAmount = history[index].removeAt(history[index].lastIndex)
                                homes[index] = (homes[index] - undoAmount).coerceAtLeast(0.0)
                            }
                        },
                        onDelete = {
                            deleteIndex = index
                            showDeleteDialog = true
                        }
                    )

                }

                Spacer(modifier = Modifier.height(20.dp))


                // 触发 Dialog 的按钮（你之前缺这个）
                Button(
                    onClick = { showAddHomeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(15.dp),

                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp   // 阴影强度
                    ),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // 输入水量
            if (showInputDialog) {
                AlertDialog(
                    onDismissRequest = { showInputDialog = false },
                    //点击后状态变为false 关闭弹窗
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    textContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),

                    title = { Text("Add Water") },

                    text = {
                        OutlinedTextField(
                            value = inputAmount,
                            onValueChange = { inputAmount = it },
                            label = { Text("Enter L") }
                        )
                    },

                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                val amount = inputAmount.toDoubleOrNull()
                                if (amount != null && amount > 0) {
                                    homes[selectedHome] += amount
                                    history[selectedHome].add(amount)   // 记录历史

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added successfully")
                                    }
                                }
                                inputAmount = ""
                                showInputDialog = false
                            }
                        ) {
                            Text("Add", color = ComposeColor.White)
                        }
                    }
                )
            }

            if (showAddHomeDialog) {
                AlertDialog(
                    onDismissRequest = { showAddHomeDialog = false },
                    //点击取消状态修改为false 弹窗消失
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = ComposeColor.LightGray,

                    title = { Text("New Home") },

                    text = {
                        OutlinedTextField(
                            value = newHomeName,
                            onValueChange = { newHomeName = it },
                            label = { Text("Enter Home Name") }
                        )
                    },

                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                if (newHomeName.isNotBlank()) {

                                    val name = newHomeName   // 关键：缓存

                                    homes.add(0.0)
                                    homeNames.add(name)
                                    history.add(mutableStateListOf())

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Home \"$name\" created")
                                    }
                                }

                                newHomeName = ""
                                showAddHomeDialog = false
                            }
                        ) {
                            Text("Create", color = ComposeColor.White)
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },

                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    textContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),

                    title = { Text("Delete Home") },

                    text = {
                        Text("This action cannot be undone.")
                    },

                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                if (deleteIndex in homes.indices &&
                                    deleteIndex in homeNames.indices &&
                                    deleteIndex in history.indices
                                ) {
                                    val deletedName = homeNames[deleteIndex]

                                    homes.removeAt(deleteIndex)
                                    homeNames.removeAt(deleteIndex)
                                    history.removeAt(deleteIndex)

                                    scope.launch {
                                        snackbarHostState.showSnackbar("Home \"$deletedName\" deleted")
                                    }
                                }

                                deleteIndex = -1
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Delete", color = ComposeColor.White)
                        }
                    },

                    dismissButton = {
                        OutlinedButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurface)                        }
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            )
        }
    }
}

// 修改后的 BottomNavigationBar
@Composable
fun BottomNavigationBar() {
    var selectedIndex by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets.navigationBars,
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { selectedIndex = 0 },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { selectedIndex = 1 },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
            label = { Text("Stats") }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { selectedIndex = 2 },
            icon = { Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = "Compare") },
            label = { Text("Compare") }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { selectedIndex = 3 },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminder") },
            label = { Text("Reminder") }
        )

        NavigationBarItem(
            selected = selectedIndex == 4,
            onClick = { selectedIndex = 4 },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Rewards") },
            label = { Text("Rewards") }
        )
    }
}

// ================= Home 专用卡片 =================
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeProgressCard(
    homeName: String,
    current: Double,
    target: Double,
    onAdd: () -> Unit,
    onUndo: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    //false默认卡片是收起的
    // 点击卡片切换展开状态（交互逻辑）
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        // Card 是 Material Design 组件 创建分组
        // 提供层级（Elevation）
        //比column更好是因为可以提供层级
        onClick = { expanded = !expanded },
        //点击卡片
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        // Elevation 表示层级
        elevation = CardDefaults.cardElevation(8.dp),
        //阴影高度 高度越大越明显
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)//卡片大小
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            //卡片内边距

            // 标题 + 按钮（全部在Card里）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = homeName,
                    fontSize = 20.sp,//home字体大小
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }

                IconButton(onClick = onUndo) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }

            if (expanded) {
                //如果展开的话更新ui
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Usage: $current / $target L",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))//卡片内容之间的间距

                val historyData = MutableList(3) { 0f }
                val progress = if (target == 0.0) 0f else (current / target).toFloat()
                historyData[2] = progress
                var startAnimation by remember { mutableStateOf(false) }
                // 状态控制卡片展开/收起
                val animatedProgress by animateFloatAsState(
                    // 动画：用于提升用户体验（smooth transition）
                    // animateFloatAsState 用于平滑数值变化
                    // progress表示进度(xx/xx) floatstate是让动画慢慢变化
                    targetValue = if (startAnimation) progress else 0f,
                    animationSpec = tween(1000),
                    label = ""
                )
                val animatedValue by animateFloatAsState(
                    targetValue = if (startAnimation) current.toFloat() else 0f,
                    animationSpec = tween(1000),
                    label = ""
                )

                LaunchedEffect(Unit) {
                    startAnimation = true
                    //true状态展开卡片 展示下面图表的动画
                }

                androidx.compose.foundation.lazy.LazyRow(
                    // 横向滑动布局（展示多个卡片）
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    // ===== 饼图 =====
                    item {
                        CardContentBox(title = "Today's Usage (L)") {
                            Box(
                                modifier = Modifier.size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Canvas 用于绘制自定义 UI（饼图 / 折线图）
                                // 提高 UI 复杂度和可视化能力
                                Canvas(modifier = Modifier.fillMaxSize()) {

                                    drawCircle(
                                        color = ComposeColor.LightGray.copy(alpha = 0.15f)
                                    )

                                    drawArc(
                                        color = primaryColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * animatedProgress,
                                        useCenter = true
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(String.format("%.1f", animatedValue))
                                    Text("/")
                                    Text(String.format("%.1f", target))
                                }
                            }
                        }
                    }

                    // ===== 折线图（恢复）=====
                    item {
                        CardContentBox(title = "Statistics") {

                            Column(
                                modifier = Modifier.width(130.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                ) {
                                    val width = size.width
                                    val height = size.height
                                    val path = androidx.compose.ui.graphics.Path()

                                    historyData.forEachIndexed { i, v ->
                                        val x = i * (width / (historyData.size - 1))
                                        val y = height - (v * height)

                                        if (i == 0) path.moveTo(x, y)
                                        else path.lineTo(x, y)

                                        drawCircle(
                                            color = primaryColor,
                                            radius = 3.dp.toPx(),
                                            center = Offset(x, y)
                                        )
                                    }

                                    drawPath(
                                        path = path,
                                        color = primaryColor,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                val now = LocalDate.now()
                                val months = (2 downTo 0).map {
                                    now.minusMonths(it.toLong())
                                        .month
                                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    months.forEach {
                                        Text(it, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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