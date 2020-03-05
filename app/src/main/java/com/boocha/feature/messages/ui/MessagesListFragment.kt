package com.boocha.feature.messages.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.messages.adapter.MessagesAdapter
import com.boocha.feature.messages.event.MessageClickEvent
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.util.OnClickLister
import kotlinx.android.synthetic.main.fragment_messages_list.*

class MessagesListFragment : BaseFragment() {

    private val currentUser: User? by lazy { arguments?.getSerializable(BUNDLE_CURRENT_USER) as User? }
    private val conversations: ArrayList<Conversation>? by lazy { arguments?.getParcelableArrayList<Conversation>(BUNDLE_CONVERSATIONS) as ArrayList<Conversation>? }

    private lateinit var messagesAdapter: MessagesAdapter

    companion object {
        private const val BUNDLE_CURRENT_USER = "bundle_current_user"
        private const val BUNDLE_CONVERSATIONS = "bundle_conversations"

        fun newInstance(currentUser: User, conversations: ArrayList<Conversation>): MessagesListFragment {
            val messagesListFragment = MessagesListFragment()
            val bundle = Bundle()

            bundle.putSerializable(BUNDLE_CURRENT_USER, currentUser)
            bundle.putParcelableArrayList(BUNDLE_CONVERSATIONS, conversations)
            messagesListFragment.arguments = bundle

            return messagesListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareMessages()
    }

    private fun prepareMessages() {
        messagesAdapter = MessagesAdapter()
        messagesAdapter.conversations = conversations!!
        messagesAdapter.currentUser = currentUser!!
        messagesAdapter.onClickLister = object : OnClickLister {
            override fun itemOnClick(view: View, position: Int) {
                postEvent(MessageClickEvent(conversations!![position]))
            }
        }

        rvConversations.apply {
            setHasFixedSize(true)
            adapter = messagesAdapter
        }
    }
}