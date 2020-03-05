package com.boocha.feature.profile

import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService

class BookListViewModel : ViewModel() {

    private val repository = Repository(FirebaseService())

    fun updateSwapStatus(userId: String, swapId: String, swapStatus: Int) {
        repository.updateSwapStatus(userId, swapId, swapStatus)
    }
}