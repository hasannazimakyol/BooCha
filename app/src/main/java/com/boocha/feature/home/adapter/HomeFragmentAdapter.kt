package com.boocha.feature.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.Swap
import com.boocha.util.OnClickLister

class HomeFragmentAdapter : RecyclerView.Adapter<HomeFragmentViewHolder>() {

    var swapList: MutableList<Swap> = ArrayList()
    lateinit var onClickLister: OnClickLister

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_swap_list, parent, false)
        return HomeFragmentViewHolder(itemView,onClickLister)
    }

    override fun getItemCount(): Int {
        return swapList.size
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        holder.holder(swapList[position])
    }
}