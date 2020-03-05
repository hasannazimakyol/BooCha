package com.boocha.feature.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.boocha.model.Swap
import com.boocha.util.OnClickLister
import com.boocha.util.dateDifference
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_swap_list.view.*

class HomeFragmentViewHolder(itemView: View, onClickLister: OnClickLister) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            onClickLister.itemOnClick(it, adapterPosition)
        }
    }

    fun holder(swap: Swap) {

        Glide.with(itemView.context).load(swap.imageUri).into(itemView.ivBook)
        if ((swap.owner?.profilePhoto.isNullOrEmpty().not())) {
            Glide.with(itemView.context).load(swap.owner?.profilePhoto).into(itemView.ivUser)
        } else {
            itemView.ivUser.visibility = View.GONE
        }

        if (swap.book?.description.isNullOrEmpty()) {
            itemView.tvBookDescription.visibility = View.GONE
        } else {
            itemView.tvBookDescription.text = swap.book?.description
        }
        itemView.tvBookName.text = swap.book?.title
        itemView.tvUsername.text = "${swap.owner?.name} ${swap.owner?.surname}"
        itemView.tvDate.text = dateDifference(swap.date ?: "")
    }
}