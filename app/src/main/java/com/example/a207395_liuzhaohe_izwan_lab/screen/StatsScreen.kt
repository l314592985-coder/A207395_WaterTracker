package com.example.a207395_liuzhaohe_izwan_lab.screen

import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeData
import java.time.LocalDate
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Path
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)

@Composable

fun StatsScreen(viewModel: WaterViewModel) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- 标题：改为与 Compare 页面完全一致 ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Statistics",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            // 使用 ViewModel 中的数据
            // ViewModel 的数据在屏幕旋转（configuration change）时不会丢失
            // 因此 UI 会自动恢复状态
            viewModel.homeList.forEach { home ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = home.home_name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(15.dp))
                        // --- 核心点：这里只调用 6 个月的折线图 ---
                        LineChartOnly(home)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LineChartOnly(home: HomeData) {
    val primaryColor = MaterialTheme.colorScheme.primary
    // 模拟 6 个月数据（前 5 个月固定 + 当前月份实时数据）
    val dataPoints = listOf(0.2f, 0.4f, 0.3f, 0.6f, 0.5f, (home.current / home.target).toFloat().coerceIn(0f, 1f))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val width = size.width
            val height = size.height
            val path = Path()

            dataPoints.forEachIndexed { i, v ->
                val x = i * (width / (dataPoints.size - 1))
                val y = height - (v * height)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }
            drawPath(path = path, color = primaryColor, style = Stroke(width = 2.dp.toPx()))
        }

        // 月份显示
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            val months = (5 downTo 0).map { LocalDate.now().minusMonths(it.toLong()).month.getDisplayName(
                TextStyle.SHORT, Locale.ENGLISH) }
            months.forEach { Text(it, fontSize = 10.sp) }
        }
    }
}
