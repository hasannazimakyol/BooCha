package com.boocha.model.book


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VolumeInfo(
        @SerializedName("averageRating")
        val averageRating: Double?,
        @SerializedName("canonicalVolumeLink")
        val canonicalVolumeLink: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("industryIdentifiers")
        val industryIdentifiers: List<IndustryIdentifier?>?,
        @SerializedName("infoLink")
        val infoLink: String?,
        @SerializedName("language")
        val language: String?,
        @SerializedName("pageCount")
        val pageCount: Int?,
        @SerializedName("previewLink")
        val previewLink: String?,
        @SerializedName("publisher")
        val publisher: String?,
        @SerializedName("publishedDate")
        val publishedDate: String?,
        @SerializedName("ratingsCount")
        val ratingsCount: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("imageLinks")
        val imageLinks: ImageLinks?,
        @SerializedName("authors")
        val authors: List<String>?
) : Parcelable