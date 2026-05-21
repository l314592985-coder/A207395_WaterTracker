package com.example.a207395_liuzhaohe_izwan_lab.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO
 *
 * Data Access Object
 *
 * 专门负责：
 *
 * 数据库增删改查
 *
 */

@Dao
interface HomeDao {

    /**
     * 插入数据
     */
    @Insert
    suspend fun insert(
        home:HomeData
    )

    /**
     * 获取全部数据
     *
     * Flow:
     * 自动监听数据库变化
     *
     * 数据变化:
     *
     * UI自动刷新
     *
     */

    @Query(
        "SELECT * FROM home_table"
    )
    fun getAllHomes():
            Flow<List<HomeData>>

    /**
     * 删除
     */

    @Delete
    suspend fun delete(
        home:HomeData
    )

    /**
     * 更新
     */

    @Update
    suspend fun update(
        home:HomeData
    )

}