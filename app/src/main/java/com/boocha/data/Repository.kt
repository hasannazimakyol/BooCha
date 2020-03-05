package com.boocha.data

import com.boocha.data.remote.FirebaseService
import com.boocha.data.remote.util.RetrofitServiceGenerator
import com.boocha.model.Swap
import com.boocha.model.User
import com.boocha.model.book.SearchResponse
import com.boocha.model.message.Conversation
import com.boocha.model.message.Message
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import java.io.File

class Repository(private val firebaseService: FirebaseService) {

    fun login(email: String, password: String, onSuccessListener: OnSuccessListener<AuthResult>, onFailureListener: OnFailureListener) {
        firebaseService.login(email, password, onSuccessListener, onFailureListener)
    }

    fun signUp(user: User, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        firebaseService.signUp(user, onSuccessListener, onFailureListener)
    }

    fun signOut() {
        firebaseService.signOut()
    }

    fun getUser(id: String, onSuccessListener: OnSuccessListener<User>, onFailureListener: OnFailureListener) {
        firebaseService.getUser(id, onSuccessListener, onFailureListener)
    }

    fun getUserSwaps(id: String, onSuccessListener: OnSuccessListener<MutableList<Swap>>, onFailureListener: OnFailureListener) {
        firebaseService.getUserSwaps(id, onSuccessListener, onFailureListener)
    }

    fun getCurrentUserAccount(): FirebaseUser? {
        return firebaseService.getCurrentUserAccount()
    }

    fun updateLastLogin() {
        return firebaseService.updateLastLogin(getCurrentUserAccount()?.uid)
    }

    fun getSwapList(onSuccessListener: OnSuccessListener<MutableList<Swap>>, onFailureListener: OnFailureListener) {
        firebaseService.getSwapList(onSuccessListener, onFailureListener)
    }

    fun searchBook(query: String): Observable<SearchResponse> {
        return RetrofitServiceGenerator.service().searchBook("intitle:$query")
    }

    fun searchBookInFirebase(bookId: String, onSuccessListener: OnSuccessListener<ArrayList<Swap?>>, onFailureListener: OnFailureListener) {
        firebaseService.searchBookInFirebase(bookId, onSuccessListener, onFailureListener)
    }

    fun addSwap(image: File, swap: Swap, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        firebaseService.addSwap(image, swap, onSuccessListener, onFailureListener)
    }

    fun getConversations(sender: User, onSuccessListener: OnSuccessListener<ArrayList<Conversation>>, onFailureListener: OnFailureListener) {
        firebaseService.getConversations(sender, onSuccessListener, onFailureListener)
    }

    fun setNewConversation(newConversation: Conversation, onSuccessListener: OnSuccessListener<Conversation>, onFailureListener: OnFailureListener) {
        firebaseService.setNewConversation(newConversation, onSuccessListener, onFailureListener)
    }

    fun sendMessage(conversationId: String, message: ArrayList<Message>?) {
        firebaseService.sendMessage(conversationId, message)
    }

    fun listenMessages(conversationId: String, onSuccessListener: OnSuccessListener<Conversation>, onFailureListener: OnFailureListener) {
        firebaseService.listenMessages(conversationId, onSuccessListener, onFailureListener)
    }

    fun updateProfilePhoto(id: String, imageFile: File, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        firebaseService.updateProfilePhoto(id, imageFile, onSuccessListener, onFailureListener)
    }

    fun updateSwapStatus(userId: String, swapId: String, swapStatus: Int) {
        firebaseService.updateSwapStatus(userId, swapId, swapStatus)
    }

    fun updateUnreadMessages(conversationId: String, message: ArrayList<Message>?) {
        firebaseService.updateUnreadMessage(conversationId, message)
    }


}