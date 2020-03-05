package com.boocha.feature.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.book.Item
import com.boocha.util.OnClickLister

class SearchAdapter : RecyclerView.Adapter<SearchViewHolder>() {

    var bookList: List<Item?> = ArrayList()
    lateinit var onClickLister: OnClickLister

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_book_list, parent, false)
        return SearchViewHolder(itemView, onClickLister)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        bookList[position]?.let {
            holder.bind(it)
        }
    }
}