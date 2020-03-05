package com.boocha.model

import android.os.Parcelable
import com.boocha.util.BOOK_STATUS_GOOD
import com.boocha.util.SWAP_STATUS_ACTIVE
import com.boocha.util.WHERE_DID_YOU_GET_STATUS_FROM_OTHERS
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Swap(
        var id: String? = "",
        var date: String? = "",
        var book: Book? = null,
        var bookStatus: Int? = BOOK_STATUS_GOOD,
        var owner: User? = null,
        var ownerDescription: String? = "",
        var imageUri: String? = "",
        var swapStatus: Int? = SWAP_STATUS_ACTIVE,
        var whereGetStatus: Int? = WHERE_DID_YOU_GET_STATUS_FROM_OTHERS
) : Parcelable