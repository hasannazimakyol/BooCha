package com.boocha.feature.messages.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.util.OnClickLister
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_messages.view.*

class MessagesViewHolder(itemView: View, onClickLister: OnClickLister) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            onClickLister.itemOnClick(it, adapterPosition)
        }
    }

    fun bind(currentUser: User, conversation: Conversation) {
        if (currentUser.id != conversation.sender?.id) {
            if (conversation.sender?.profilePhoto.isNullOrEmpty().not()) {
                itemView.ivProfilePhoto.visibility = View.VISIBLE
                Glide.with(itemView.context).load(conversation.sender?.profilePhoto).into(itemView.ivProfilePhoto)
            } else {

                itemView.ivProfilePhoto.visibility = View.GONE
            }
            itemView.tvUsername.text = "${conversation.sender?.name} ${conversation.sender?.surname}"
        } else {
            if (conversation.receiver?.profilePhoto.isNullOrEmpty().not()) {
                itemView.ivProfilePhoto.visibility = View.VISIBLE
                Glide.with(itemView.context).load(conversation.receiver?.profilePhoto).into(itemView.ivProfilePhoto)
            } else {
                itemView.ivProfilePhoto.visibility = View.GONE
            }
            itemView.tvUsername.text = "${conversation.receiver?.name} ${conversation.receiver?.surname}"
        }

        var unreadMessageCount = 0
        conversation.messages?.forEach {
            if (it.owner != currentUser.id && !it.isRead) {
                unreadMessageCount++
            }
        }

        if (unreadMessageCount != 0) {
            itemView.tvUnreadMessageCount.visibility = View.VISIBLE
            itemView.tvUnreadMessageCount.text = unreadMessageCount.toString()
        } else {
            itemView.tvUnreadMessageCount.visibility = View.GONE
        }

        itemView.tvBookName.text = conversation.swap?.book?.title
    }
}