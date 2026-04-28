package com.example.a207395_liuzhaohe_izwan_lab.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import kotlinx.coroutines.launch

@Composable
fun ReminderScreen(viewModel: WaterViewModel) { //去掉括号里的参数
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图：保持一致
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
                Text(
                    text = "Reminders",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ⭐ 使用本地状态代替 ViewModel
            // 这个数据只在当前页面有效
            // 屏幕旋转后会丢失（和 ViewModel 的区别）
            /**
            val homeList = remember {
            mutableStateListOf(
            HomeData("Home1", 0.0),
            HomeData("Home2", 0.0),
            HomeData("Home3", 0.0)
            )
            }
            // ⭐ 使用本地数据
            homeList.forEach { home ->
                ExpandableReminderCard(
             */

            // 遍历 Home 列表
            // 使用 ViewModel 中的数据
            // ViewModel 的数据在屏幕旋转（configuration change）时不会丢失
            // 因此 UI 会自动恢复状态
            viewModel.homeList.forEach { home ->
                ExpandableReminderCard(
                    homeName = home.home_name,
                    onStatusChange = { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
        )
    }
}

@Composable
fun ExpandableReminderCard(homeName: String, onStatusChange: (String) -> Unit) {
    // ⭐ 修改点 1：默认展开（原来是 false）
    var expanded by remember { mutableStateOf(true) } // ✅ card默认展开
    // 状态 2 & 3: 内部的两个具体开关
    // UI 状态（只在当前 Composable 中使用）
    // 当界面重建（如旋转屏幕）时可能会丢失
    var overLimitEnabled by remember { mutableStateOf(true) }
    var addWaterEnabled by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        // ⭐ 修改点 3：移除 clickable（禁止折叠/展开）
        // .clickable { expanded = !expanded }  ❌ 已删除
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // --- 标题 ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = homeName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // ⭐ 修改点 5：始终显示内容（不再依赖 expanded）
            // 原本：AnimatedVisibility(visible = expanded)
            // 现在：直接显示
            Column {
                Divider(
                    modifier = Modifier.padding(vertical = 15.dp),
                    thickness = 0.5.dp
                )

                // 设置项 1: 今日用水量超标提醒
                ReminderSwitchRow(
                    label = "Daily water usage exceeded alert", // ✅ 改为英文
                    checked = overLimitEnabled,
                    onCheckedChange = {
                        overLimitEnabled = it
                        onStatusChange("$homeName: Over-limit alert ${if (it) "enabled" else "disabled"}") // ✅ 英文提示
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 设置项 2: 添加用水量提醒
                ReminderSwitchRow(
                    label = "Water intake reminder", // ✅ 改为英文
                    checked = addWaterEnabled,
                    onCheckedChange = {
                        addWaterEnabled = it
                        onStatusChange("$homeName: Intake reminder ${if (it) "enabled" else "disabled"}") // ✅ 英文提示
                    }
                )
            }
        }
    }
}

@Composable
fun ReminderSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}