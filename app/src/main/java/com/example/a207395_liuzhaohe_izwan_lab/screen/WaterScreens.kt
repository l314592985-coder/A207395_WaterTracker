package com.example.a207395_liuzhaohe_izwan_lab.screen

import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.a207395_liuzhaohe_izwan_lab.CardContentBox
import com.example.a207395_liuzhaohe_izwan_lab.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterTrackerScreen(viewModel: WaterViewModel) {
    // --- 核心：删除了旧的 homes, homeNames, history 变量 ---
    var selectedHome by remember { mutableIntStateOf(0) }
    var showInputDialog by remember { mutableStateOf(false) }
    var inputAmount by remember { mutableStateOf("") }
    var showAddHomeDialog by remember { mutableStateOf(false) }
    var newHomeName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteIndex by remember { mutableIntStateOf(-1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 注意：这里的 Scaffold 已经由 MainActivity 处理了，如果你还要保留背景图，
    // 建议只保留 Box 及其内部逻辑
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
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Water Tracker", color = MaterialTheme.colorScheme.onBackground, fontSize = 35.sp, fontWeight = FontWeight.ExtraBold)
                if (viewModel.avatarUri != null) {
                    AsyncImage(
                        model = viewModel.avatarUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- 修改点：遍历 viewModel 里的数据 ---
            // --- 修改位置：WaterTrackerScreen 函数内部的循环部分 ---
            viewModel.homeList.forEachIndexed { index, home ->
                HomeProgressCard(
                    homeName = home.home_name,
                    current = home.current, // 这里的 current 现在是由 ViewModel 驱动的
                    target = home.target,
                    onAdd = {
                        selectedHome = index
                        showInputDialog = true
                    },
                    onUndo = {
                        // 1. 执行业务逻辑
                        viewModel.undoWater(index)

                        // 2. 触发提示（必须在 scope.launch 里执行）
                        scope.launch {
                            snackbarHostState.showSnackbar("Last water entry has been undone")
                        }
                    },
                    onDelete = {
                        // 这里只做两件事：记录 index，打开弹窗
                        deleteIndex = index
                        showDeleteDialog = true
                    }
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { showAddHomeDialog = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(15.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp   // 阴影强度
                ),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, null)
                Text("Add New Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))

        }

        // Dialog 部分：逻辑需要改为调用 viewModel.addWater / viewModel.addHome
        if (showInputDialog) {
            AlertDialog(
                onDismissRequest = { showInputDialog = false },
                title = { Text("Add Water") },
                text = { OutlinedTextField(value = inputAmount, onValueChange = { inputAmount = it }, label = { Text("Enter L") }) },
                confirmButton = {
                    Button(onClick = {
                        val amount = inputAmount.toDoubleOrNull()
                        if (amount != null) {
                            viewModel.addWater(selectedHome, amount)
                            // 关键点：弹出提示
                            scope.launch {
                                snackbarHostState.showSnackbar("Successfully added $amount L of water!")
                            }
                        }
                        inputAmount = ""
                        showInputDialog = false
                    }) { Text("Add") }
                }
            )
        }

        // --- 修改位置：WaterTrackerScreen 内部的 showDeleteDialog 逻辑 ---
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Home") },
                text = { Text("Are you sure you want to delete \"${viewModel.homeList.getOrNull(deleteIndex)?.home_name ?: ""}\"?") },
                confirmButton = {
                    Button(onClick = {
                        if (deleteIndex != -1) {
                            viewModel.deleteHome(deleteIndex)
                            // 关键点：弹出提示
                            scope.launch {
                                snackbarHostState.showSnackbar("Home deleted successfully")
                            }
                        }
                        showDeleteDialog = false
                        deleteIndex = -1
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // --- 添加 Home 的弹窗逻辑 ---
        if (showAddHomeDialog) {
            AlertDialog(
                onDismissRequest = { showAddHomeDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground,

                title = { Text("Add New Home", fontWeight = FontWeight.Bold) },

                text = {
                    Column {
                        Text("Enter a name for the new tracking card:", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newHomeName,
                            onValueChange = { newHomeName = it },
                            label = { Text("Home Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },

                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            if (newHomeName.isNotBlank()) {
                                // 1. 【关键修复】先用一个常量把名字存起来，防止清空后 Snackbar 读不到
                                val savedName = newHomeName

                                // 2. 调用 ViewModel 添加 Home
                                viewModel.addHome(savedName)

                                // 3. 弹出 Snackbar，使用刚才存好的 savedName
                                scope.launch {
                                    snackbarHostState.showSnackbar("Home \"$savedName\" created successfully")
                                }

                                // 4. 最后再清空输入框和关闭弹窗
                                newHomeName = ""
                                showAddHomeDialog = false
                            }
                        }

                    ) {
                        Text("Create", color = Color.White)
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            newHomeName = ""
                            showAddHomeDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter) // 放在顶部
                .padding(top = 10.dp)      // 往下挪一点，不要被状态栏或刘海屏挡住
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
                /**val titleText = if (current > 0.0)
                "${current.toInt()} L"
                else
                homeName
                Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
                )*/

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
                val primaryColor = MaterialTheme.colorScheme.primary
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

                LaunchedEffect(Unit) {
                    startAnimation = true
                    //true状态展开卡片 展示下面图表的动画
                }

                LazyRow(
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
                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // ✅ 只使用变量，不调用 MaterialTheme
                                    drawCircle(
                                        color = surfaceVariant.copy(alpha = 0.15f)
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

                    // ===== 折线图 =====
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
                                    val path = Path()

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
