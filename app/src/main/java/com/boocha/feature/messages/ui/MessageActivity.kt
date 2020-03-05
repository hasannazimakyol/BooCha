package com.boocha.feature.messages.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseActivity
import com.boocha.data.remote.util.Status
import com.boocha.feature.home.ui.ProfileFragment
import com.boocha.feature.messages.event.MessageClickEvent
import com.boocha.feature.messages.event.UserClickEvent
import com.boocha.feature.messages.viewmodel.MessageActivityViewModel
import com.boocha.model.Swap
import com.boocha.model.User
import com.boocha.model.message.Conversation
import com.boocha.model.message.Message
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MessageActivity : BaseActivity() {

    private lateinit var viewModel: MessageActivityViewModel

    private var swap: Swap? = null
    private var receiver: User? = null
    private var sender: User? = null
    private var openedFrom: String? = null
    private var conversations: ArrayList<Conversation> = ArrayList()

    companion object {

        const val OPENED_FROM_SWAP_DETAIL_ACTIVITY = "opened_from_swap_detail_activity"
        const val OPENED_FROM_HOME_FRAGMENT = "opened_from_home_fragment"

        private const val EXTRA_OPENED_FROM = "extra_opened_from"
        private const val EXTRA_SWAP = "extra_swap"

        fun newIntent(context: Context, openedFrom: String, swap: Swap? = null): Intent {

            return Intent(context, MessageActivity::class.java).also {
                it.putExtra(EXTRA_OPENED_FROM, openedFrom)
                it.putExtra(EXTRA_SWAP, swap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_message)

        viewModel = ViewModelProviders.of(this).get(MessageActivityViewModel::class.java)
        initConversationsLiveData()
        initNewConversationLiveData()

        swap = intent.getParcelableExtra(EXTRA_SWAP) as Swap?
        receiver = swap?.owner
        sender = viewModel.getSenderUser(this)
        openedFrom = intent.getStringExtra(EXTRA_OPENED_FROM)


        sender?.let {
            viewModel.getConversations(it)
        }
    }

    override fun onStart() {
        super.onStart()
        registerToEvent()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterToEvent()
    }

    private fun initConversationsLiveData() {
        viewModel.conversationLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    resource.data?.let {
                        conversations = it
                    }

                    when (openedFrom) {
                        OPENED_FROM_SWAP_DETAIL_ACTIVITY -> {
                            navigateToMessageDetail()
                        }
                        OPENED_FROM_HOME_FRAGMENT -> {
                            navigateToMessagesList()
                        }
                    }
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                    showErrorDialog(resource.message)
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun initNewConversationLiveData() {
        viewModel.newConversationLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    replaceFragment(R.id.fragmentHolder, MessageDetailFragment.newInstance(sender!!, resource.data), null, false)
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                    showErrorDialog(resource.message)
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun navigateToMessageDetail() {
        val conversation = conversations.filter { it.swap?.id == swap?.id }
        if (conversation.isEmpty().not()) {
            replaceFragment(R.id.fragmentHolder, MessageDetailFragment.newInstance(sender!!, conversation[0]), null, false)
        } else {
            val messages = ArrayList<Message>()
            val newConversation = Conversation("", sender!!, swap?.owner!!, swap!!, messages)

            viewModel.setNewConversation(newConversation)
        }
    }

    private fun navigateToMessagesList() {
        replaceFragment(R.id.fragmentHolder, MessagesListFragment.newInstance(sender!!, conversations), null, false)
    }

    private fun navigateToProfile(id: String) {
        replaceFragment(R.id.fragmentHolder, ProfileFragment.newInstance(id), ProfileFragment.TAG, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageClickEvent(event: MessageClickEvent) {
        replaceFragment(R.id.fragmentHolder, MessageDetailFragment.newInstance(sender!!, event.conversation), MessageDetailFragment.javaClass.simpleName, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserClickEvent(event: UserClickEvent) {
        navigateToProfile(event.id)
    }
}