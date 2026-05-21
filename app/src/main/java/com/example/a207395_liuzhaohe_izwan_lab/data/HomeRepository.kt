package com.example.a207395_liuzhaohe_izwan_lab.data

class HomeRepository(
    private val dao:HomeDao
){

    val allHomes=
        dao.getAllHomes()

    suspend fun insert(
        home:HomeData
    ){
        dao.insert(home)
    }

    suspend fun delete(
        home:HomeData
    ){
        dao.delete(home)
    }

    suspend fun update(
        home:HomeData
    ){
        dao.update(home)
    }

}