package com.boocha.feature.messages.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.model.message.Conversation
import com.boocha.model.message.Message
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class MessageDetailFragmentViewModel : ViewModel() {

    val messagesLiveData = MutableLiveData<Resource<Conversation>>()

    val repository = Repository(FirebaseService())

    fun listenMessages(conversationId: String) {
        repository.listenMessages(conversationId
                , OnSuccessListener {
            messagesLiveData.value = Resource.success(it)
        }
                , OnFailureListener {
            messagesLiveData.value = Resource.error(it.localizedMessage, null)

        })
    }

    fun sendMessage(conversationId: String, message: ArrayList<Message>?) {
        repository.sendMessage(conversationId, message)
    }

    fun updateUnreadMessages(conversationId: String, message: ArrayList<Message>?) {
        repository.updateUnreadMessages(conversationId, message)
    }
}