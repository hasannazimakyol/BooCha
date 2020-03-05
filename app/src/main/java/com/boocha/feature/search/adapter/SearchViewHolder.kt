package com.boocha.feature.search.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.boocha.model.book.Item
import com.boocha.util.OnClickLister
import kotlinx.android.synthetic.main.item_book_list.view.*

class SearchViewHolder(itemView: View, onClickLister: OnClickLister) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            onClickLister.itemOnClick(it, adapterPosition)
        }
    }

    fun bind(item: Item) {
        itemView.tvBookName.text = item.volumeInfo?.title ?: ""
        itemView.tvAuthorName.text = item.volumeInfo?.authors?.get(0) ?: ""
        itemView.tvBookDescription.text = item.volumeInfo?.description ?: ""
        itemView.tvBookPublisher.text = "Publisher: ${item.volumeInfo?.publisher ?: ""} "
    }
}