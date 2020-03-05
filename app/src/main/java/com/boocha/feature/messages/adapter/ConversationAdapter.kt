package com.boocha.feature.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boocha.R
import com.boocha.model.User
import com.boocha.model.message.Conversation

class ConversationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var currentUser: User
    lateinit var conversation: Conversation

    companion object {
        const val VIEW_TYPE_INCOMING_MESSAGE = 0
        const val VIEW_TYPE_OUTGOING_MESSAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_INCOMING_MESSAGE -> {
                val item = LayoutInflater.from(parent.context).inflate(R.layout.item_speech_bubble_left, parent, false)
                InComingViewHolder(item)
            }
            VIEW_TYPE_OUTGOING_MESSAGE -> {
                val item = LayoutInflater.from(parent.context).inflate(R.layout.item_speech_bubble_right, parent, false)
                OutGoingViewHolder(item)
            }
            else -> {
                val item = LayoutInflater.from(parent.context).inflate(R.layout.item_speech_bubble_left, parent, false)
                ConversationViewHolder(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return conversation.messages?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InComingViewHolder) {
            holder.bind(conversation.messages!![position])
        } else if (holder is OutGoingViewHolder) {
            holder.bind(conversation.messages!![position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentUser.id == conversation.messages!![position].owner) {
            VIEW_TYPE_OUTGOING_MESSAGE
        } else {
            VIEW_TYPE_INCOMING_MESSAGE
        }
    }
}