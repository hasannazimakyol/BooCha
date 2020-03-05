package com.boocha.feature.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.util.OnClickLister

class MessagesAdapter : RecyclerView.Adapter<MessagesViewHolder>() {

    lateinit var conversations: ArrayList<Conversation>
    lateinit var currentUser: User
    lateinit var onClickLister: OnClickLister

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_messages, parent, false), onClickLister)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(currentUser, conversations[position])
    }

}