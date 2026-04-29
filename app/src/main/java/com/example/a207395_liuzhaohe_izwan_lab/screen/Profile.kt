package com.example.a207395_liuzhaohe_izwan_lab.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.a207395_liuzhaohe_izwan_lab.R
import com.example.a207395_liuzhaohe_izwan_lab.viewmodel.WaterViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(viewModel: WaterViewModel) {
//================= 新增：系统相册选择器 =================
    // rememberLauncherForActivityResult：
    // Android 官方提供的 Activity 返回结果接收器
    // 用途：打开系统相册 选完图片 把图片 Uri 返回给我们
    // ActivityResultContracts.GetContent()：表示从系统文件里选内容
    // launch("image/*")：表示只允许选择图片*/
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateAvatar(it)
        }
    }

    /**================= 新增状态 =================
     * remember：
     * 用来记住 UI 状态
     * 比如输入框里的文字：
     * 输入一次后不会因为重组而消失*/
    var inputLimit by remember {
        mutableStateOf(viewModel.dailyLimit.toInt().toString())
    }

    /**Snackbar 状态控制器
     * 用来弹顶部提示*/
    val snackbarHostState = remember { SnackbarHostState() }

    /**协程作用域
     * Snackbar 需要在协程里调用*/
    val scope = rememberCoroutineScope()

    /**Box：最外层容器（类似 FrameLayout）
     * 作用：
     * 1. 允许多个组件“叠加”显示（背景图 + 内容）
     * 2. fillMaxSize() = 占满整个屏幕*/
    Box(modifier = Modifier.fillMaxSize()) {

        /**Image：背景图
         * 作用：
         * 1. 给整个页面加背景
         * 2. 提升 UI 美观度（和其他页面保持一致）*/
        Image(
            painter = painterResource(R.drawable.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.1F
        )

        /**Column：垂直布局（从上往下排）
         * 作用：
         * 1. 控制页面结构（Header + 内容）*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 25.dp)
        ) {

            /**===== Header（顶部区域）=====
             * Row：横向布局
             * 这里用 Row 是为了控制“水平居中”*/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                /**Image：头像
                 * 功能：
                 * 1. 显示用户头像
                 * 2. 作为 Profile 页面核心视觉元素*/
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            launcher.launch("image/*")
                        }
                ) {
                    if (viewModel.avatarUri != null) {
                        AsyncImage(
                            model = viewModel.avatarUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.avatar),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Spacer：空白间距
            Spacer(modifier = Modifier.height(50.dp))

            /**================= 新增 Card =================
             * Card 参数完全参考 Water 页面 HomeProgressCard
             * shape：
             * 圆角大小一致
             * elevation：
             * 阴影一致
             * fillMaxWidth：
             * 宽度一致*/
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                // Card 内部内容
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Daily Water Limit",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // 输入框 用户输入新的 daily limit
                    OutlinedTextField(
                        value = inputLimit,
                        onValueChange = {
                            inputLimit = it
                        },
                        label = {
                            Text("Enter new limit (L)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 更新按钮
                    Button(
                        onClick = {
                            val newLimit = inputLimit.toDoubleOrNull()

                            if (newLimit != null && newLimit > 0) {
                                viewModel.updateDailyLimit(newLimit)
                                inputLimit = newLimit.toInt().toString()

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Daily limit updated to ${newLimit.toInt()} L"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(15.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Update",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // 顶部提示
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        )
    }
}