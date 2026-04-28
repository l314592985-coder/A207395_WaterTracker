package com.example.a207395_liuzhaohe_izwan_lab.data

import androidx.compose.runtime.mutableStateListOf

// 定义数据类 (Task 2)
// 检查你的数据类定义
data class HomeData(
    val home_name: String, // 字段1
    var current: Double, // 字段2
    /**target：每日用水上限
     * 原来这里是 val（只读）
     * val 创建后就不能修改
     * 现在改成 var（可修改）
     * 因为 Profile 页面需要动态修改每日上限
     * 比如：
     * 200L -> 250L -> 300L
     * WaterScreen 的：
     * Usage: current / target
     * 饼图的：
     * current / target
     * 都会跟着自动更新*/
    var target: Double = 200.0,
    // 字段4：用于存储历史记录，供 Stats 和 Compare 页面共享
    val history: MutableList<Double> = mutableStateListOf(),
    // --- 新增：提醒开关状态 ---
    // 数据模型中的字段（属于应用数据）
    // 通常由 ViewModel 管理，可长期保存
    var isTargetAlertEnabled: Boolean = false,
    var isActivityAlertEnabled: Boolean = false
)