package com.boocha.feature.messages.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boocha.data.Repository
import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.Resource
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.util.WriteObjectFile
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class MessageActivityViewModel : ViewModel() {

    private val repository = Repository(FirebaseService())

    val conversationLiveData = MutableLiveData<Resource<ArrayList<Conversation>>>()
    val newConversationLiveData = MutableLiveData<Resource<Conversation>>()

    fun getSenderUser(context: Context) = WriteObjectFile(context).readObject(WriteObjectFile.FILE_USER) as User?

    fun getConversations(sender: User) {
        conversationLiveData.value = Resource.loading(null)
        repository.getConversations(sender
                , OnSuccessListener {
            conversationLiveData.value = Resource.success(it)
        }
                , OnFailureListener {
            conversationLiveData.value = Resource.error(it.localizedMessage ?: "", null)
        })
    }

    fun setNewConversation(newConversation: Conversation) {
        newConversationLiveData.value = Resource.loading(null)
        repository.setNewConversation(newConversation
                , OnSuccessListener {
            newConversationLiveData.value = Resource.success(it)
        }
                , OnFailureListener {
            newConversationLiveData.value = Resource.error(it.localizedMessage ?: "", null)
        })
    }
}