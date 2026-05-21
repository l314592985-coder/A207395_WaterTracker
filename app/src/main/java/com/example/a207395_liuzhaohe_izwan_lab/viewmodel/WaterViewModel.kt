package com.example.a207395_liuzhaohe_izwan_lab.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeData
import com.example.a207395_liuzhaohe_izwan_lab.data.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel：
 * 负责管理整个 App 数据
 *
 * Lab4:
 *
 * UI
 * ↓
 * ViewModel
 *
 * Lab5:
 *
 * UI
 * ↓
 * ViewModel
 * ↓
 * Repository
 * ↓
 * DAO
 * ↓
 * Room
 *
 */

class WaterViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    /**
     * ================= Room 数据 =================
     *
     * MutableStateFlow：
     *
     * 可以理解成：
     *
     * 会自动更新的变量
     *
     * Room 数据变化：
     *
     * 数据库
     * ↓
     * Flow
     * ↓
     * StateFlow
     * ↓
     * UI刷新
     *
     */

    private val _homeList =
        MutableStateFlow<List<HomeData>>(
            emptyList()
        )

    val homeList: StateFlow<List<HomeData>>
            = _homeList.asStateFlow()

    init {

        viewModelScope.launch {

            repository.allHomes.collectLatest { homes ->

                /**
                 * 数据库变化
                 * 自动刷新 UI
                 */

                _homeList.value = homes

            }

        }

    }

    var avatarUri by mutableStateOf<Uri?>(null)

    fun updateAvatar(uri: Uri) {
        avatarUri = uri
    }

    var dailyLimit by mutableDoubleStateOf(
        200.0
    )

    /**
     * ================= 新增 Undo 历史记录 =================
     *
     * key：
     * Home索引
     *
     * value：
     * 每次输入的水量
     *
     * 例：
     *
     * Home1:
     * [5,2,3]
     *
     * Undo:
     *
     * removeLast()
     * -> 3
     * -> 2
     * -> 5
     */

    fun updateDailyLimit(
        newLimit: Double
    ) {

        if (newLimit <= 0)
            return

        dailyLimit = newLimit

        /**
         * 遍历所有 Home
         * 修改 target
         */

        viewModelScope.launch {

            homeList.value.forEach {

                    home ->

                repository.update(

                    home.copy(
                        target = newLimit
                    )

                )

            }

        }

    }

    /**
     * 添加 Home
     */

    fun addHome(
        name: String
    ) {

        viewModelScope.launch {

            repository.insert(

                HomeData(
                    home_name = name,
                    current = 0.0,
                    target = dailyLimit
                )

            )

        }

    }

    /**
     * 添加用水
     */

    fun addWater(
        index:Int,
        amount:Double
    ){

        if(amount<=0)
            return

        if(index !in homeList.value.indices)
            return

        val home=
            homeList.value[index]

        /**
         * 当前历史
         */

        val currentHistory=

            if(home.history.isBlank())

                mutableListOf()

            else

                home.history
                    .split(",")

                    .map{
                        it.toDouble()
                    }

                    .toMutableList()

        /**
         * 添加记录
         */

        currentHistory.add(
            amount
        )

        viewModelScope.launch{

            repository.update(

                home.copy(

                    current=
                        home.current+amount,

                    history=

                        currentHistory.joinToString(
                            ","
                        )

                )

            )

        }

    }

    /**
     * Undo
     */

    fun undoWater(
        index:Int
    ){

        if(index !in homeList.value.indices)
            return

        val home=
            homeList.value[index]

        val history=

            if(home.history.isBlank())

                mutableListOf()

            else

                home.history
                    .split(",")

                    .map{
                        it.toDouble()
                    }

                    .toMutableList()

        if(
            history.isEmpty()
        ) return

        val lastAmount=

            history.removeAt(
                history.lastIndex
            )

        viewModelScope.launch{

            repository.update(

                home.copy(

                    current=

                        (
                                home.current
                                        -
                                        lastAmount
                                )

                            .coerceAtLeast(
                                0.0
                            ),

                    history=

                        history.joinToString(
                            ","
                        )

                )

            )

        }

    }

    /**
     * 删除
     */

    fun deleteHome(
        index:Int
    ){

        if(index !in homeList.value.indices)
            return

        viewModelScope.launch{

            repository.delete(
                homeList.value[index]
            )

        }
    }

}