package com.boocha.data.remote

import android.util.Log
import androidx.core.net.toUri
import com.boocha.data.remote.util.CONVERSATIONS
import com.boocha.data.remote.util.SWAP_LIST
import com.boocha.data.remote.util.USERS
import com.boocha.model.Swap
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.model.message.Message
import com.boocha.util.SWAP_STATUS_ACTIVE
import com.boocha.util.getCurrentTime
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class FirebaseService {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private val writeBatch = database.batch()

    fun login(email: String, password: String, onSuccessListener: OnSuccessListener<AuthResult>, onFailureListener: OnFailureListener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { response ->
                    firebaseAuth.currentUser?.also {
                        updateLastLogin(it.uid)
                    }
                    onSuccessListener.onSuccess(response)
                }
                .addOnFailureListener { exception ->
                    onFailureListener.onFailure(exception)
                }
    }

    fun signUp(user: User, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        firebaseAuth.createUserWithEmailAndPassword(user.email ?: "", user.password ?: "")
                .addOnSuccessListener {
                    user.id = firebaseAuth.currentUser?.uid ?: ""
                    saveUserToDatabase(user, onSuccessListener, onFailureListener)
                }
                .addOnFailureListener { exception ->
                    onFailureListener.onFailure(exception)
                }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getUser(id: String, onSuccessListener: OnSuccessListener<User>, onFailureListener: OnFailureListener) {
        database.collection(USERS).document(id).get()
                .addOnSuccessListener { user ->
                    onSuccessListener.onSuccess(snapshotToUser(user))
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun getUserSwaps(id: String, onSuccessListener: OnSuccessListener<MutableList<Swap>>, onFailureListener: OnFailureListener) {
        database.collection(USERS).document(id).collection(SWAP_LIST).get()
                .addOnSuccessListener {
                    val swaps = it.toObjects(Swap::class.java)
                    getUser(id, OnSuccessListener {
                        for (swap in swaps) {
                            swap.owner = it
                        }

                        onSuccessListener.onSuccess(swaps)
                    }, OnFailureListener {

                    })
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun getCurrentUserAccount(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun updateLastLogin(id: String?) {
        if (id != null) {
            database.collection(USERS).document(id).update("lastLogin", getCurrentTime())
        }
    }

    fun getSwapList(onSuccessListener: OnSuccessListener<MutableList<Swap>>, onFailureListener: OnFailureListener) {
        database.collection(SWAP_LIST).orderBy("date", Query.Direction.DESCENDING).limit(40).get()
                .addOnSuccessListener { swaps ->
                    val filteredSwaps = swaps.toObjects(Swap::class.java).filter { it.swapStatus == SWAP_STATUS_ACTIVE }
                    onSuccessListener.onSuccess(filteredSwaps as MutableList<Swap>?)
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun searchBook(name: String) {
        database.collection("books").orderBy("name").startAt(name).get()
                .addOnSuccessListener {
                    Log.e("", "")
                }
                .addOnFailureListener {
                    Log.e("", "")
                }
    }

    fun searchBookInFirebase(bookId: String, onSuccessListener: OnSuccessListener<ArrayList<Swap?>>, onFailureListener: OnFailureListener) {
        database.collection(SWAP_LIST).whereEqualTo("book.id", bookId).get()
                .addOnSuccessListener {
                    val books = it.toObjects(Swap::class.java)

                    if (books.size == 0) {
                        onFailureListener.onFailure(Exception())
                    } else {
                        val arrayList = ArrayList<Swap?>()
                        for (book in books) {
                            arrayList.add(book)
                        }

                        onSuccessListener.onSuccess(arrayList)
                    }
                }.addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun addSwap(image: File, swap: Swap, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        val id = UUID.randomUUID()

        val referance = storageRef.child("bookPhotos/$id")
        val uploadTask = storageRef.child("bookPhotos/$id").putFile(image.toUri())
                .addOnSuccessListener {
                    referance.downloadUrl.addOnSuccessListener { uri ->
                        swap.imageUri = uri.toString()

                        val documentId = UUID.randomUUID()
                        swap.id = documentId.toString()
                        writeBatch.set(database.collection(SWAP_LIST).document(documentId.toString()), swap)
                        writeBatch.set(database.collection(USERS).document(getCurrentUserAccount()?.uid!!).collection(SWAP_LIST).document(documentId.toString()), swap)

                        writeBatch.commit().addOnSuccessListener {
                            onSuccessListener.onSuccess(it)
                        }.addOnFailureListener {
                            onFailureListener.onFailure(it)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("", "")
                }
    }

    fun getConversations(sender: User, onSuccessListener: OnSuccessListener<ArrayList<Conversation>>, onFailureListener: OnFailureListener) {
        database.collection(CONVERSATIONS).whereEqualTo("sender.id", sender.id).get()
                .addOnSuccessListener {
                    val conversationArrayList = ArrayList<Conversation>()
                    val senderConversation = it.toObjects(Conversation::class.java)
                    if (senderConversation.isEmpty().not()) {
                        for (conversation in senderConversation) {
                            conversationArrayList.add(conversation)
                        }
                    }

                    database.collection(CONVERSATIONS).whereEqualTo("receiver.id", sender.id).get()
                            .addOnSuccessListener {
                                val receiverConversation = it.toObjects(Conversation::class.java)
                                if (receiverConversation.isEmpty().not()) {
                                    for (conversation in receiverConversation) {
                                        conversationArrayList.add(conversation)
                                    }
                                }
                                onSuccessListener.onSuccess(conversationArrayList)
                            }
                            .addOnFailureListener {
                                onFailureListener.onFailure(it)
                            }
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun setNewConversation(newConversation: Conversation, onSuccessListener: OnSuccessListener<Conversation>, onFailureListener: OnFailureListener) {
        newConversation.id = UUID.randomUUID().toString()
        database.collection(CONVERSATIONS).document(newConversation.id!!).set(newConversation)
                .addOnSuccessListener {
                    onSuccessListener.onSuccess(newConversation)
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }

    fun sendMessage(conversationId: String, message: ArrayList<Message>?) {
        database.collection(CONVERSATIONS).document(conversationId).update("messages", message)
    }

    fun updateUnreadMessage(conversationId: String, message: ArrayList<Message>?) {
        database.collection(CONVERSATIONS).document(conversationId).update("messages", message)
    }

    fun listenMessages(conversationId: String, onSuccessListener: OnSuccessListener<Conversation>, onFailureListener: OnFailureListener) {
        database.collection(CONVERSATIONS).document(conversationId).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            val conversation = documentSnapshot?.toObject(Conversation::class.java)
            onSuccessListener.onSuccess(conversation)
        }
    }

    fun updateProfilePhoto(userId: String, imageFile: File, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        val id = UUID.randomUUID()

        val reference = storageRef.child("profilePhotos/$id")
        storageRef.child("profilePhotos/$id").putFile(imageFile.toUri())
                .addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener { profilePhotoUri ->

                        writeBatch.update(database.collection(USERS).document(userId), "profilePhoto", profilePhotoUri.toString())

                        database.collection(SWAP_LIST).whereEqualTo("owner.id", userId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.update("owner.profilePhoto", profilePhotoUri.toString())
                            }
                        }

                        database.collection(CONVERSATIONS).whereEqualTo("receiver.id", userId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.update("receiver.profilePhoto", profilePhotoUri.toString())
                            }
                        }

                        database.collection(CONVERSATIONS).whereEqualTo("sender.id", userId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.update("sender.profilePhoto", profilePhotoUri.toString())
                            }
                        }

                        database.collection(CONVERSATIONS).whereEqualTo("swap.owner.id", userId).get().addOnSuccessListener {
                            for (document in it.documents) {
                                document.reference.update("swap.owner.profilePhoto", profilePhotoUri.toString())
                            }
                        }

                        writeBatch.commit()
                                .addOnSuccessListener { onSuccessListener.onSuccess(it) }
                                .addOnFailureListener { onFailureListener.onFailure(it) }

                    }
                            .addOnFailureListener { onFailureListener.onFailure(it) }
                }
                .addOnFailureListener { onFailureListener.onFailure(it) }
    }

    fun updateSwapStatus(userId: String, swapId: String, swapStatus: Int) {
        val currentDate = getCurrentTime()

        if (swapStatus == SWAP_STATUS_ACTIVE) {
            database.collection(USERS).document(userId).collection(SWAP_LIST).document(swapId).update("date", currentDate)
            database.collection(SWAP_LIST).document(swapId).update("date", currentDate)
        }

        database.collection(USERS).document(userId).collection(SWAP_LIST).document(swapId).update("swapStatus", swapStatus)
        database.collection(SWAP_LIST).document(swapId).update("swapStatus", swapStatus)
        database.collection(CONVERSATIONS).get().addOnSuccessListener {
            val conversations = it.toObjects(Conversation::class.java).filter { it.swap?.id == swapId }

            for (conversation in conversations) {
                database.collection(CONVERSATIONS).document(conversation.id!!).update("swap.swapStatus", swapStatus)
                if (swapStatus == SWAP_STATUS_ACTIVE) {
                    database.collection(CONVERSATIONS).document(conversation.id!!).update("swap.date", currentDate)
                }
            }
        }
    }

    private fun snapshotToUser(snapshot: DocumentSnapshot): User {
        val id = snapshot.id
        val name = snapshot.get("name") as String
        val surname = snapshot.get("surname") as String
        val profilePhoto = snapshot.get("profilePhoto") as String
        val email = snapshot.get("email") as String
        val password = snapshot.get("password") as String
        val lastLogin = snapshot.get("lastLogin") as String

        return User(id, name, surname, profilePhoto, email, password, lastLogin)
    }

    private fun snapshotToBook(snapshot: DocumentSnapshot) {
        val bookId = snapshot.id
        val name = snapshot.get("name") as String
        val author = snapshot.get("author") as String
        val page = snapshot.get("page") as String
        val photo = snapshot.get("photo") as String
        val description = snapshot.get("description") as String

        //return Book(bookId, name, author, photo, page, description)
    }

    private fun saveUserToDatabase(user: User, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        val data: HashMap<String, String> = hashMapOf(
                "name" to user.name,
                "surname" to user.surname,
                "profilePhoto" to user.profilePhoto,
                "email" to user.email,
                "password" to user.password,
                "lastLogin" to getCurrentTime()
        )

        database.collection(USERS).document(user.id ?: "").set(data)
                .addOnSuccessListener {
                    onSuccessListener.onSuccess(it)
                    signOut()
                }
                .addOnFailureListener {
                    onFailureListener.onFailure(it)
                }
    }
}


