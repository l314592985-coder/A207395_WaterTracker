package com.example.a207395_liuzhaohe_izwan_lab.repository

import com.example.a207395_liuzhaohe_izwan_lab.model.CommunityData
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val db =
        FirebaseFirestore.getInstance()

    fun uploadAchievement(
        homeName:String,
        current:Double,
        target:Double
    ){
        val achievement =
            hashMapOf(
                "homeName" to homeName,
                "current" to current,
                "target" to target,
                "timestamp" to
                        System.currentTimeMillis()
            )
        db.collection("community")
            .add(achievement)
    }

    fun loadCommunity(
        onResult:(List<CommunityData>)->Unit
    ){
        db.collection("community")
            .orderBy(
                "timestamp"
            )
            .get()
            .addOnSuccessListener {
                val list =
                    mutableListOf<CommunityData>()
                for(document in it){
                    val item =
                        document.toObject(
                            CommunityData::class.java
                        )
                    list.add(item)
                }

                onResult(list.reversed())
            }
    }
}