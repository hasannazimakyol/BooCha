package com.boocha.feature.profile.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.Swap
import com.boocha.util.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_swap_list_profile.view.*

class BookListViewHolder(itemView: View, private val onClickListener: OnClickLister?) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            onClickListener?.itemOnClick(it, adapterPosition)
        }
    }

    fun bind(swap: Swap, currentUser: Boolean) {
        Glide.with(itemView.context).load(swap.imageUri).into(itemView.ivBook)

        itemView.tvBookName.text = swap.book?.title
        itemView.tvAuthorName.text = swap.book?.author
        itemView.tvDescription.text = swap.ownerDescription
        itemView.tvBookStatus.text = bookStatus(swap.bookStatus)
        itemView.ibMenu.setOnClickListener {
            onClickListener?.itemOnClick(it, adapterPosition)
        }

        if (currentUser) {
            itemView.ibMenu.visibility = View.GONE

        } else {
            itemView.ibMenu.visibility = View.VISIBLE

        }

        if (swap.whereGetStatus == WHERE_DID_YOU_GET_STATUS_FROM_BOOCHA && swap.swapStatus == SWAP_STATUS_LIBRARY) {
            itemView.ivTag.visibility = View.VISIBLE
        } else {
            itemView.ivTag.visibility = View.GONE
        }

        itemView.ivTag.setOnClickListener {
            Toast.makeText(itemView.context, itemView.context.getString(R.string.this_book_was_taken_from_boo_cha), Toast.LENGTH_LONG).show()
        }
    }

    private fun bookStatus(bookStatus: Int?): String {
        return when (bookStatus) {
            BOOK_STATUS_NOT_GOOD -> {
                "Book Status: Not Good"
            }
            BOOK_STATUS_GOOD -> {
                "Book Status: Good"

            }
            BOOK_STATUS_PERFECT -> {
                "Book Status: Perfect"
            }
            else -> {
                ""
            }
        }
    }
}