package com.example.a207395_liuzhaohe_izwan_lab.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeData

/**ViewModel：负责管理应用的数据（数据层）
 * ⭐ 作用：
 * 1. 存储所有 Home 数据（homeList）
 * 2. 提供操作数据的方法（add / undo / delete）
 * 3. 在屏幕旋转（横屏/竖屏）时数据不会丢失
 * 👉 所有页面（Compare / Reminder / Stats）都共享这个 ViewModel*/
class WaterViewModel : ViewModel() {

    /**================= 新增：用户头像状态 =================
     * avatarUri：
     * 用来保存用户从相册选择的头像路径
     *
     * Uri 可以理解为：
     * Android 系统里的文件地址
     *
     * 比如：
     * content://media/external/images/xxx
     *
     * mutableStateOf：
     * Compose 的状态容器
     *
     * 当头像改变时：
     * 所有读取这个状态的 UI 会自动刷新
     *
     * 初始值 null：
     * 表示还没有选头像
     * 此时显示默认 avatar.png
     */
    var avatarUri by mutableStateOf<Uri?>(null)

    /**更新头像
     * 功能：
     * Profile 页面选完图片后调用这里
     *
     * 然后整个 App 共享头像一起刷新
     */
    fun updateAvatar(uri: Uri) {
        avatarUri = uri
    }

    /**================= 新增：全局每日用水上限 =================
     * mutableDoubleStateOf：
     * 是 Compose 的状态容器（State）
     * 当这个值变化时：
     * 所有读取它的 UI 都会自动刷新
     * 比如：
     * Profile 页面把 200 改成 300
     * Water 页面会立刻更新：
     * 1）Usage 分母变化
     * 2）饼图比例变化
     * 3）新建 Home 默认 target 变化
     * by：
     * Kotlin 委托语法
     * 这样可以直接：
     * dailyLimit = 300.0
     * 不需要写：
     * dailyLimit.value = 300.0*/
    var dailyLimit by mutableDoubleStateOf(200.0)

    /**homeList：存储所有家庭（Home）的数据
     * mutableStateListOf：
     * 👉 是“可观察列表”（Jetpack Compose 会监听它的变化）
     * 👉 一旦数据改变，UI 会自动更新（不需要手动刷新）*/
    var homeList = mutableStateListOf<HomeData>(

        // 初始化 3 个默认家庭
        HomeData("Home1", 0.0, dailyLimit),
        HomeData("Home2", 0.0, dailyLimit),
        HomeData("Home3", 0.0, dailyLimit)
    )

    /**================= 新增：更新每日上限 =================
     * 功能：
     * Profile 页面调用这个函数修改 limit
     * 比如：
     * updateDailyLimit(300.0)
     * 效果：
     * 所有 Home 的 target 都会同步变成 300
     */
    fun updateDailyLimit(newLimit: Double) {
        // 防止输入 0 或负数
        if (newLimit <= 0) return

        // 更新全局 limit
        dailyLimit = newLimit

        /**同步更新所有 Home 的 target
         * 为什么要循环：
         * 因为每张卡片都有自己的 target
         * Home1.target
         * Home2.target
         * Home3.target
         * 都要同步修改*/
        homeList.forEachIndexed { index, home ->

            /**copy：
             * 复制旧对象，并修改其中一个字段
             * Compose 更容易侦测这种变化
             * UI 刷新更稳定*/
            homeList[index] = home.copy(
                target = newLimit
            )
        }
    }

    /**添加新的 Home
     * @param name 用户输入的家庭名称*/
    fun addHome(name: String) {

        // 向列表中添加一个新的 HomeData 对象
        // 默认 current 用水量为 0.0
        // target 使用最新 dailyLimit
        homeList.add(
            HomeData(
                name,
                0.0,
                dailyLimit
            )
        )
    }

    /**添加用水量
     * @param index 表示第几个 Home（列表位置）
     * @param amount 要增加的用水量*/
    fun addWater(index: Int, amount: Double) {
        // 当前用水量增加
        homeList[index].current += amount

        // 同时把这次操作记录到历史列表（用于 undo）
        homeList[index].history.add(amount)
    }

    /**撤销（Undo）上一次加水操作
     * @param index 表示第几个 Home*/
    fun undoWater(index: Int) {

        // ⭐ 第一步：检查 index 是否合法（防止崩溃）
        if (index in homeList.indices) {
            val home = homeList[index]

            // ⭐ 第二步：确保有历史记录（否则无法撤销）
            if (home.history.isNotEmpty()) {

                // 取出最后一次加水的数值
                val lastAmount = home.history.removeAt(home.history.lastIndex)

                /**⭐ 第三步：更新 current
                 * coerceAtLeast(0.0)：
                 * 确保不会减成负数*/
                homeList[index] = home.copy(
                    current = (home.current - lastAmount).coerceAtLeast(0.0)
                )
            }
        }
    }

    /**删除某个 Home
     * @param index 表示第几个 Home*/
    fun deleteHome(index: Int) {
        // 检查 index 是否有效
        if (index in homeList.indices) {

            // 从列表中移除该 Home
            homeList.removeAt(index)
        }
    }
}