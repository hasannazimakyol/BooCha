package com.boocha.feature.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.data.remote.util.Status
import com.boocha.model.Swap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class HomeFragmentViewModel : ViewModel() {

    val swapListLiveData = MutableLiveData<Resource<MutableList<Swap>>>()

    private val repository = Repository(FirebaseService())

    fun getSwapList() {
        swapListLiveData.value = Resource(Status.LOADING, null, null)
        repository.getSwapList(OnSuccessListener {
            swapListLiveData.value = Resource(Status.SUCCESS, it, null)
        }, OnFailureListener {
            swapListLiveData.value = Resource(Status.ERROR, null, it.localizedMessage)
        })
    }
}