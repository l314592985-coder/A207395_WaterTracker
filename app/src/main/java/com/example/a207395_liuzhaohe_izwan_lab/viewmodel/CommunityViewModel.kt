package com.example.a207395_liuzhaohe_izwan_lab.viewmodel

import androidx.lifecycle.ViewModel
import com.example.a207395_liuzhaohe_izwan_lab.model.CommunityData
import com.example.a207395_liuzhaohe_izwan_lab.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommunityViewModel : ViewModel() {
    private val repository =
        FirebaseRepository()

    private val _communityList =
        MutableStateFlow<List<CommunityData>>(
            emptyList()
        )

    val communityList:StateFlow<List<CommunityData>>
            = _communityList

    fun loadCommunity(){
        repository.loadCommunity {
            _communityList.value = it
        }
    }
}