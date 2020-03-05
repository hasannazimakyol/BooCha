package com.boocha.model.message

import android.os.Parcelable
import com.boocha.model.Swap
import com.boocha.model.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Conversation(var id: String? = null, val sender: User? = null, val receiver: User? = null, val swap: Swap? = null, var messages: ArrayList<Message>? = null) : Parcelable