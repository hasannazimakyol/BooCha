package com.boocha.feature.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.data.remote.util.Status
import com.boocha.model.Swap
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class SearchActivityViewModel : ViewModel() {

    private val repository = Repository(FirebaseService())

    val searchBookInFirebaseLiveData = MutableLiveData<Resource<ArrayList<Swap?>>>()

    fun searchBookInFirebase(bookId: String) {
        searchBookInFirebaseLiveData.value = Resource(Status.LOADING, null, null)

        repository.searchBookInFirebase(bookId
                , OnSuccessListener {
            searchBookInFirebaseLiveData.value = Resource(Status.SUCCESS, it, null)

        }
                , OnFailureListener {
            searchBookInFirebaseLiveData.value = Resource(Status.ERROR, null, it.localizedMessage)
        })
    }
}