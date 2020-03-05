package com.boocha.feature.swapdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.Swap
import com.boocha.util.OnClickLister

class OwnerUsersAdapter : RecyclerView.Adapter<OwnerUsersViewHolder>() {

    var swaps: List<Swap?> = ArrayList()
    lateinit var onClickLister: OnClickLister

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerUsersViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_owner_users, parent, false)
        return OwnerUsersViewHolder(itemView, onClickLister)
    }

    override fun getItemCount(): Int {
        return swaps.size
    }

    override fun onBindViewHolder(holder: OwnerUsersViewHolder, position: Int) {
        swaps[position]?.let {
            holder.bind(it)
        }
    }
}