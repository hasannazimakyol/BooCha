package com.boocha.feature.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.data.remote.util.Status
import com.boocha.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser

class LoginActivityViewModel : ViewModel() {

    val loginLiveData = MutableLiveData<Resource<String?>>()
    val signUpLiveData = MutableLiveData<Resource<String?>>()
    private val repository = Repository(FirebaseService())

    fun login(email: String, password: String) {
        loginLiveData.value = Resource(Status.LOADING, null, null)
        repository.login(email, password, OnSuccessListener {
            loginLiveData.value = Resource(Status.SUCCESS, null, null)
        }, OnFailureListener {
            loginLiveData.value = Resource(Status.ERROR, it.message, null)

        })
    }

    fun signUp(user: User) {
        signUpLiveData.value = Resource(Status.LOADING, null, null)

        repository.signUp(user, OnSuccessListener {
            signUpLiveData.value = Resource(Status.SUCCESS, null, null)
        }, OnFailureListener {
            signUpLiveData.value = Resource(Status.ERROR, it.message, null)
        })
    }

    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUserAccount()
    }
}