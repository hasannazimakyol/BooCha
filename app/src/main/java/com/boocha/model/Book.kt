package com.boocha.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book(
        var id: String = "",
        val title: String = "",
        val author: String = "",
        val description: String = "",
        val publisher: String = "",
        val publishedDate: String = "",
        val isbn13: String = "",
        val language: String = "",
        val pageCount: String = ""
) : Parcelable