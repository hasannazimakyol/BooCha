package com.boocha.model.book


import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items")
    val items: List<Item?>?,
    @SerializedName("totalItems")
    val totalItems: Int?
)