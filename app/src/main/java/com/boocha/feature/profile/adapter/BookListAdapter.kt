package com.boocha.feature.profile.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.Swap
import com.boocha.util.OnClickLister

class BookListAdapter : RecyclerView.Adapter<BookListViewHolder>() {

    var bookList: List<Swap> = ArrayList()
    var isCurrentUser: Boolean = true;
    var onClickListener: OnClickLister? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_swap_list_profile, parent, false)
        return BookListViewHolder(itemView, onClickListener)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(bookList[position],isCurrentUser)
    }
}