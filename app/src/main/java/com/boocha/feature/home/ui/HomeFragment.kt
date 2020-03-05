package com.boocha.feature.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.data.remote.util.Status
import com.boocha.feature.home.adapter.HomeFragmentAdapter
import com.boocha.feature.home.event.UnreadMessageCountEvent
import com.boocha.feature.home.viewmodel.HomeFragmentViewModel
import com.boocha.feature.messages.ui.MessageActivity
import com.boocha.feature.search.ui.SearchActivity
import com.boocha.feature.swapdetail.ui.SwapDetailActivity
import com.boocha.model.Swap
import com.boocha.util.OnClickLister
import kotlinx.android.synthetic.main.home_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : BaseFragment() {

    lateinit var viewModel: HomeFragmentViewModel
    lateinit var swapListAdapter: HomeFragmentAdapter
    lateinit var swaps: MutableList<Swap>

    companion object {

        const val TAG = "home_fragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        initOnClickListener()
        initSwapListLiveData()
        prepareSwapList()

        viewModel.getSwapList()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun initOnClickListener() {
        tvSearch.setOnClickListener {
            context?.let {
                startActivity(SearchActivity.newIntent(it, SearchActivity.OPENED_FROM_HOME_FRAGMENT))
            }
        }

        ibMessages.setOnClickListener {
            context?.let {
                startActivity(MessageActivity.newIntent(it, MessageActivity.OPENED_FROM_HOME_FRAGMENT))
            }
        }
    }

    private fun initSwapListLiveData() {
        viewModel.swapListLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    if (resource.data != null) {
                        swaps = resource.data
                        swapListAdapter.swapList = resource.data
                        swapListAdapter.notifyDataSetChanged()
                        dismissLoadingDialog()
                    }
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun prepareSwapList() {
        swapListAdapter = HomeFragmentAdapter()
        swapListAdapter.onClickLister = object : OnClickLister {
            override fun itemOnClick(view: View, position: Int) {
                swaps[position].let { swap ->
                    context?.let { context ->
                        startActivity(SwapDetailActivity.newIntent(context, SwapDetailActivity.OPENED_FROM_HOME_ACTIVITY, swap = swap))
                    }
                }
            }
        }

        rvSwapList.apply {
            setHasFixedSize(true)
            adapter = swapListAdapter
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnreadMessageCountEvent(event: UnreadMessageCountEvent) {
        if (event.unreadMessageCount == 0) {
            tvUnreadMessageCount.visibility = View.GONE
        } else {
            tvUnreadMessageCount.visibility = View.VISIBLE
            tvUnreadMessageCount.text = event.unreadMessageCount.toString()
        }
    }
}