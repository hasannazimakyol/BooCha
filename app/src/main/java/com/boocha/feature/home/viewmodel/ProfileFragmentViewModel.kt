package com.boocha.feature.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.data.remote.util.Status
import com.boocha.model.Swap
import com.boocha.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import java.io.File

class ProfileFragmentViewModel : ViewModel() {

    val userLiveData = MutableLiveData<Resource<User?>>()
    val swapsLiveData = MutableLiveData<Resource<ArrayList<Swap>>>()
    val updateProfilePhotoLiveData = MutableLiveData<Resource<File>>()

    private val repository = Repository(FirebaseService())

    fun getUser(id: String) {
        userLiveData.value = Resource(Status.LOADING, null, null)

        repository.getUser(id, OnSuccessListener { user ->
            userLiveData.value = Resource(Status.SUCCESS, user, null)

        }, OnFailureListener {
            userLiveData.value = Resource(Status.ERROR, null, null)
        })
    }

    fun getUserSwaps(id: String) {
        repository.getUserSwaps(id, OnSuccessListener { mutableList ->
            val swapList: ArrayList<Swap> = ArrayList()

            for (swap in mutableList) {
                swapList.add(swap)
            }

            swapsLiveData.value = Resource(Status.SUCCESS, swapList, null)
        }, OnFailureListener {
            swapsLiveData.value = Resource(Status.ERROR, null, it.localizedMessage)
        })
    }

    fun getCurrentUserAccount(): FirebaseUser? {
        return repository.getCurrentUserAccount()
    }

    fun signOut() {
        repository.signOut()
    }

    fun updateProfilePhoto(id: String, imageFile: File) {
        updateProfilePhotoLiveData.value = Resource.loading(null)
        repository.updateProfilePhoto(id, imageFile
                , OnSuccessListener {
            updateProfilePhotoLiveData.value = Resource.success(imageFile)

        }, OnFailureListener {
            updateProfilePhotoLiveData.value = Resource.error(it.localizedMessage, null)

        })
    }
}