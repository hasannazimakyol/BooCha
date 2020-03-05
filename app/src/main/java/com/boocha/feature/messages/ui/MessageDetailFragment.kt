package com.boocha.feature.messages.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.data.remote.util.Status
import com.boocha.feature.messages.adapter.ConversationAdapter
import com.boocha.feature.messages.event.UserClickEvent
import com.boocha.feature.messages.viewmodel.MessageDetailFragmentViewModel
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.model.message.Message
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_message_detail.*

class MessageDetailFragment : BaseFragment() {

    private lateinit var viewModel: MessageDetailFragmentViewModel
    private lateinit var conversationAdapter: ConversationAdapter

    private val currentUser: User? by lazy { arguments?.getSerializable(BUNDLE_CURRENT_USER) as User? }
    private val conversation: Conversation? by lazy { arguments?.getParcelable(BUNDLE_CONVERSATION) as Conversation? }

    companion object {

        private const val BUNDLE_CURRENT_USER = "bundle_current_user"
        private const val BUNDLE_CONVERSATION = "bundle_conversation"

        fun newInstance(currentUser: User, conversation: Conversation?): MessageDetailFragment {
            val messageDetailFragment = MessageDetailFragment()
            val bundle = Bundle()

            bundle.putSerializable(BUNDLE_CURRENT_USER, currentUser)
            bundle.putParcelable(BUNDLE_CONVERSATION, conversation)
            messageDetailFragment.arguments = bundle

            return messageDetailFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MessageDetailFragmentViewModel::class.java)
        viewModel.listenMessages(conversation?.id!!)

        prepareUi()
        initMessagesLiveData()
        initOnClickListener()
        prepareConversation()
    }

    override fun onStop() {
        updateUnreadMessages()
        super.onStop()
    }

    private fun prepareUi() {
        if (currentUser?.id != conversation?.sender?.id) {
            tvTitle.text = "${conversation?.sender?.name} ${conversation?.sender?.surname}"
            val photo = conversation?.sender?.profilePhoto
            if (photo.isNullOrEmpty()) {
                ivSenderPhoto.visibility = View.GONE
            } else {
                ivSenderPhoto.visibility = View.VISIBLE
                Glide.with(context!!).load(photo).into(ivSenderPhoto)
            }
        } else {
            tvTitle.text = "${conversation?.receiver?.name} ${conversation?.receiver?.surname}"
            val photo = conversation?.receiver?.profilePhoto
            if (photo.isNullOrEmpty()) {
                ivSenderPhoto.visibility = View.GONE
            } else {
                ivSenderPhoto.visibility = View.VISIBLE
                Glide.with(context!!).load(photo).into(ivSenderPhoto)
            }
        }
    }

    private fun initOnClickListener() {
        btnSend.setOnClickListener {
            val message = Message(currentUser?.id, etMessage.text.toString())
            conversation?.messages?.add(message)
            etMessage.setText("")
            viewModel.sendMessage(conversation?.id!!, conversation!!.messages)
        }

        toolbar.setOnClickListener {
            if (conversation?.sender?.id != currentUser?.id) {
                postEvent(UserClickEvent(conversation?.sender?.id!!))
            } else {
                postEvent(UserClickEvent(conversation?.receiver?.id!!))
            }
        }
    }

    private fun initMessagesLiveData() {
        viewModel.messagesLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    conversation?.messages = resource.data?.messages
                    conversationAdapter.conversation = resource.data!!
                    conversationAdapter.notifyDataSetChanged()
                    conversationAdapter.conversation.messages?.size?.let {
                        if (it != 0) {
                            rvConversation.smoothScrollToPosition((it.minus(1)))
                        }
                    }
                }
            }
        })
    }

    private fun prepareConversation() {
        conversationAdapter = ConversationAdapter()
        conversationAdapter.currentUser = currentUser!!
        conversationAdapter.conversation = conversation!!

        rvConversation.apply {
            adapter = conversationAdapter
        }
    }

    private fun updateUnreadMessages() {
        conversation?.messages?.forEach {
            if (it.owner != currentUser?.id) {
                it.isRead = true
            }
        }
        viewModel.updateUnreadMessages(conversation?.id!!, conversation?.messages)
    }
}