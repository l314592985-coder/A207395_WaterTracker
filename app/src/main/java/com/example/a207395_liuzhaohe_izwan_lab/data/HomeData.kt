package com.example.a207395_liuzhaohe_izwan_lab.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ================= Room Entity =================
 *
 * Entity：
 * 可以理解为数据库中的一张表（Table）
 *
 * 以前：
 * HomeData 只是普通 Kotlin 对象
 *
 * 现在：
 * HomeData 会自动生成数据库表
 *
 * 表结构大概会变成：
 *
 * id | home_name | current | target | history
 *
 */

@Entity(tableName="home_table")
data class HomeData(

    /**
     * PrimaryKey：
     * 主键
     *
     * autoGenerate=true：
     * Room 自动编号
     */
    @PrimaryKey(autoGenerate=true)
    val id:Int=0,

    val home_name:String,

    var current:Double,

    var target:Double=200.0,

    /**
     * 保存历史记录
     *
     * 格式：
     *
     * 5,2,3
     *
     * 表示：
     *
     * 第一次 +5L
     * 第二次 +2L
     * 第三次 +3L
     *
     * Undo：
     *
     * 删除最后一个值
     */

    var history:String="",

    var isTargetAlertEnabled:Boolean=false,

    var isActivityAlertEnabled:Boolean=false
)