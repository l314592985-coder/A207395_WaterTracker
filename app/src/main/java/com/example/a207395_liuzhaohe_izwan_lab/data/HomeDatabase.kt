package com.example.a207395_liuzhaohe_izwan_lab.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database
 *
 * App数据库中心
 *
 */

@Database(
    entities=[HomeData::class],
    version=2
)

abstract class HomeDatabase:
    RoomDatabase(){

    abstract fun homeDao():
            HomeDao

    companion object{

        @Volatile
        private var INSTANCE:
                HomeDatabase?=null

        fun getDatabase(
            context:Context
        ):HomeDatabase{

            return INSTANCE?: synchronized(this){

                val instance=
                    Room.databaseBuilder(

                        context,

                        HomeDatabase::class.java,

                        "home_database"

                    )

                        .fallbackToDestructiveMigration()

                        .build()

                INSTANCE=instance

                instance

            }

        }

    }

}