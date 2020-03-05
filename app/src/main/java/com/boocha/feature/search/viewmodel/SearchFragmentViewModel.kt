package com.boocha.feature.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.data.remote.util.Status
import com.boocha.model.book.SearchResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchFragmentViewModel : ViewModel() {

    val searchBookLiveData = MutableLiveData<Resource<SearchResponse>>()

    private val repository = Repository(FirebaseService())

    fun searchBook(query: String) {
        searchBookLiveData.value = Resource(Status.LOADING, null, "")
        repository.searchBook(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->
                    searchBookLiveData.value = Resource(Status.SUCCESS, response, "")
                }
    }
}