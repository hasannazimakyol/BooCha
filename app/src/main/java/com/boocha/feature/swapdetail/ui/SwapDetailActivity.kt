package com.boocha.feature.swapdetail.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boocha.R
import com.boocha.base.BaseActivity
import com.boocha.feature.home.ui.ProfileFragment
import com.boocha.feature.messages.event.UserClickEvent
import com.boocha.feature.messages.ui.MessageActivity
import com.boocha.feature.swapdetail.event.BookOwnerClickEvent
import com.boocha.feature.swapdetail.event.SendSwapRequestClickEvent
import com.boocha.model.Swap
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SwapDetailActivity : BaseActivity() {

    private val openedFrom: String by lazy { intent.getStringExtra(EXTRA_OPENED_FROM) }
    private val swap: Swap by lazy { intent.getParcelableExtra(EXTRA_SWAP) as Swap }
    private val swaps: ArrayList<Swap?>? by lazy { intent.getParcelableArrayListExtra<Swap>(EXTRA_SWAPS) as ArrayList<Swap?>? }

    companion object {
        const val OPENED_FROM_HOME_ACTIVITY = "opened_from_home_activity"
        const val OPENED_FROM_SEARCH_ACTIVITY = "opened_from_search_activity"

        private const val EXTRA_OPENED_FROM = "extra_opened_from"
        private const val EXTRA_SWAP = "extra_swap"
        private const val EXTRA_SWAPS = "extra_swaps"


        fun newIntent(context: Context, openedFrom: String, swap: Swap? = null, swaps: ArrayList<Swap?>? = null): Intent {
            val intent = Intent(context, SwapDetailActivity::class.java)
            intent.putExtra(EXTRA_OPENED_FROM, openedFrom)
            intent.putExtra(EXTRA_SWAP, swap)
            intent.putParcelableArrayListExtra(EXTRA_SWAPS, swaps)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_swap_detail)

        when (openedFrom) {
            OPENED_FROM_HOME_ACTIVITY -> {
                navigateSwapDetailFragment(swap)
            }
            OPENED_FROM_SEARCH_ACTIVITY -> {
                navigateBookOwnersFragment(swaps)
            }
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

    private fun navigateSwapDetailFragment(swap: Swap) {
        replaceFragment(R.id.fragmentHolder, SwapDetailFragment.newInstance(swap), null, false)
    }

    private fun navigateBookOwnersFragment(swaps: ArrayList<Swap?>?) {
        replaceFragment(R.id.fragmentHolder, BookOwnersFragment.newInstance(swaps), null, false)
    }

    private fun navigateToProfile(id: String) {
        replaceFragment(R.id.fragmentHolder, ProfileFragment.newInstance(id), ProfileFragment.TAG, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBookOwnerClickEvent(event: BookOwnerClickEvent) {
        event.swap?.let {
            navigateSwapDetailFragment(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendSwapRequestClickEvent(event: SendSwapRequestClickEvent) {
        startActivity(MessageActivity.newIntent(this, MessageActivity.OPENED_FROM_SWAP_DETAIL_ACTIVITY, swap))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserClickEvent(event: UserClickEvent) {
        navigateToProfile(event.id)
    }
}