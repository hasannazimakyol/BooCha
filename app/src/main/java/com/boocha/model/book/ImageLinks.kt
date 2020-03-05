package com.boocha.model.book

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageLinks(
        @SerializedName("smallThumbnail")
        val smallThumbnail: String? = null,
        @SerializedName("thumbnail")
        val thumbnail: String? = null
) : Parcelable