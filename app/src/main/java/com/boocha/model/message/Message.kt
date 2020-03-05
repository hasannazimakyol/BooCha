package com.boocha.model.message

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(val owner: String? = null, val message: String? = null, var isRead: Boolean = false) : Parcelable