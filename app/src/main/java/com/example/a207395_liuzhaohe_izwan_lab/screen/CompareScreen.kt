package com.example.a207395_liuzhaohe_izwan_lab4.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 【Task 1: Define Screens】
 * 对比页面：风格完全参考 StatsScreen。
 * 运行逻辑：遍历 viewModel 中的所有数据项，并将每一项与国家标准线进行双线对比。
 */
@Composable
fun CompareScreen(viewModel: WaterViewModel) {
    // 状态管理：控制对比的基准水平（低/中/高）
    var selectedLevel by remember { mutableStateOf(230f) }
    // 用来控制“顶部提示”的显示
    val snackbarHostState = remember { SnackbarHostState() }
    // 用来启动协程（Snackbar 必须在协程里调用）
    val scope = rememberCoroutineScope()

    // ✅ 新增：外层 Box（关键）
    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ 背景图（你给的代码）
        Image(
            painter = painterResource(R.drawable.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.1F
        )

        // ✅ 原来的 Column 原封不动放进来
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- 标题：改为与 WaterTracker 页面完全一致 ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Comparison",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- 选择器：用于切换对比基准（已去掉紫色 Card 背景）---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Select Benchmark ( L/Day )", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val levels = listOf(
                        "Low (4950L)" to 160f,
                        "Mid (5400L)" to 230f,
                        "High (6900L)" to 300f
                    )

                    levels.forEach { (label, value) ->
                        Button(
                            onClick = {
                                // ⭐ 只在“真的切换”时才触发
                                // 避免一直点同一个按钮也弹提示
                                if (selectedLevel != value) {
                                    // 更新当前选中的标准（Low / Mid / High）
                                    selectedLevel = value
                                    // ⭐ 根据数值判断当前是哪个等级
                                    val label = when (value) {
                                        160f -> "Low"
                                        230f -> "Mid"
                                        300f -> "High"
                                        else -> ""
                                    }
                                    // ⭐ 显示顶部提示（核心）
                                    scope.launch {
                                        // 这一行会触发顶部 Snackbar
                                        snackbarHostState.showSnackbar(
                                            "Switched to $label standard"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLevel == value)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.LightGray
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            /**
             * 【Task 2: ViewModel Integration】
             * 运行逻辑：遍历 viewModel.homeList。
             * 调用逻辑：为每一个 Home 项生成一个对比卡片，实现全数据同步。
             */
            // 使用 ViewModel 中的数据
            // ViewModel 的数据在屏幕旋转（configuration change）时不会丢失
            // 因此 UI 会自动恢复状态
            viewModel.homeList.forEach { home ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    // 使用 surface 颜色和 4.dp 阴影，与 Stats 页面完全对齐
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = home.home_name, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(15.dp))

                        // --- 核心：调用双线对比组件 ---
                        CompareLineChart(home, selectedLevel)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // ⭐ 顶部提示 UI 容器
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter) // 👉 放在顶部中间
                .padding(top = 10.dp)       // 👉 往下挪一点（避免挡住状态栏）
        )
    }
}

/**
 * 核心绘图组件：在一个图表内绘制两条线
 */
@Composable
fun CompareLineChart(
    home: com.example.a207395_liuzhaohe_izwan_lab.data.HomeData,
    benchmark: Float   // 👈 当前选中的值（160 / 230 / 300）
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    // ⭐ 第一步：让 benchmark 变成“动画值”
    // animateFloatAsState 会自动帮你：
    // 从“旧值”平滑过渡到“新值”
    val animatedBenchmark by animateFloatAsState(

        // 👇 目标值（你点击按钮后会改变）
        targetValue = benchmark,

        // 👇 动画配置（时间 + 动画曲线）
        animationSpec = tween(
            durationMillis = 600,              // 动画时长 600ms
            easing = FastOutSlowInEasing       // 动画曲线（先慢→快→慢）
        ),

        label = "benchmarkAnimation" // 可忽略（调试用）
    )

    // ⭐ 第二步：你的原数据（不变）
    val safeValue = (home.current / 400f)
        .toFloat()
        .coerceIn(0f, 1f)
    val dataPoints = listOf(0.3f, 0.45f, 0.4f, 0.55f, 0.5f, safeValue)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val width = size.width
            val height = size.height
            // ⭐ 第三步：用“动画值”计算 Y 坐标
            // 原来是 benchmark，现在改成 animatedBenchmark
            val benchY = height - (animatedBenchmark / 400f * height)

            // ⭐ 第四步：画“会动的虚线”
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, benchY),
                end = Offset(width, benchY),
                strokeWidth = 2.dp.toPx(),

                // 👇 虚线效果
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(15f, 15f),
                    0f
                )
            )

            // ⭐ 原有用户数据曲线（不动）
            val path = Path()
            dataPoints.forEachIndexed { i, v ->
                val x = i * (width / (dataPoints.size - 1))
                val y = height - (v * height)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // ⭐ 月份显示（你原本的逻辑）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            val calendar = java.util.Calendar.getInstance()

            val formatter = remember {
                java.text.SimpleDateFormat("MMM", Locale.ENGLISH)
            }

            val months = (5 downTo 0).map {
                val cal = java.util.Calendar.getInstance()
                cal.time = calendar.time
                cal.add(java.util.Calendar.MONTH, -it)
                formatter.format(cal.time)
            }

            months.forEach {
                Text(it, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}