package com.boocha.model.book


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
        @SerializedName("id")
        val id: String?,
        @SerializedName("volumeInfo")
        val volumeInfo: VolumeInfo?
) : Parcelable