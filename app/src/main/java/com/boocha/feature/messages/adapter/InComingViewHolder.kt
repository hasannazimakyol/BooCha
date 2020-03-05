package com.boocha.feature.messages.adapter


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.boocha.model.message.Message
import kotlinx.android.synthetic.main.item_speech_bubble_left.view.*

class InComingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message) {
        itemView.tvMessage.text = message.message
    }
}