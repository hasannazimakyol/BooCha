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
import java.io.File

class AddBookForSwapFragmentViewModel : ViewModel() {

    private val repository = Repository(FirebaseService())

    val addSwapLiveData = MutableLiveData<Resource<Boolean>>()

    fun addSwap(image: File, swap: Swap) {
        addSwapLiveData.value = Resource(Status.LOADING, null, null)
        repository.addSwap(image, swap, OnSuccessListener {
            addSwapLiveData.value = Resource(Status.SUCCESS, null, null)

        }, OnFailureListener {
            addSwapLiveData.value = Resource(Status.ERROR, null, it.localizedMessage)
        })
    }
}