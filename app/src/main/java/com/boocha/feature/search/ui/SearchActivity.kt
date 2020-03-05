package com.boocha.feature.search.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseActivity
import com.boocha.data.remote.util.Status
import com.boocha.feature.search.BookItemClickEvent
import com.boocha.feature.search.viewmodel.SearchActivityViewModel
import com.boocha.feature.swapdetail.ui.SwapDetailActivity
import com.boocha.model.Swap
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchActivity : BaseActivity() {

    lateinit var viewModel: SearchActivityViewModel

    private val openedFrom: String by lazy { intent.getStringExtra(EXTRA_OPENED_FROM) }

    companion object {

        const val OPENED_FROM_HOME_FRAGMENT = "opened_from_home_fragment"
        const val OPENED_FROM_ADD_BOOK_FOR_SWAP_FRAGMENT = "opened_from_add_book_for_swap_fragmnet"

        const val EXTRA_BOOK_ITEM = "extra_book_item"
        const val EXTRA_OPENED_FROM = "extra_opened_from"

        fun newIntent(context: Context, openedFrom: String): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(EXTRA_OPENED_FROM, openedFrom)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(this).get(SearchActivityViewModel::class.java)


        initSearchBookLiveData()
        navigateToSearchFragment()
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

    private fun initSearchBookLiveData() {
        viewModel.searchBookInFirebaseLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    navigateToSwapDetail(resource.data)
                }
                Status.ERROR -> {
                    dismissLoadingDialog()

                    AlertDialog.Builder(this)
                            .setTitle(getString(R.string.opps))
                            .setMessage(getString(R.string.we_did_not_found_this_book_for_swap))
                            .setPositiveButton(getText(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .setCancelable(true)
                            .show()
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun navigateToSearchFragment() {
        replaceFragment(R.id.fragmentHolder, SearchFragment.newInstance(), null, false)
    }

    private fun navigateToSwapDetail(swaps: ArrayList<Swap?>?) {
        startActivity(SwapDetailActivity.newIntent(this, SwapDetailActivity.OPENED_FROM_SEARCH_ACTIVITY, swaps = swaps))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBookItemClickEvent(event: BookItemClickEvent) {
        when (openedFrom) {
            OPENED_FROM_HOME_FRAGMENT -> {
                event.item.id?.let { id ->
                    viewModel.searchBookInFirebase(id)
                }
            }
            OPENED_FROM_ADD_BOOK_FOR_SWAP_FRAGMENT -> {
                val intent = Intent()
                intent.putExtra(EXTRA_BOOK_ITEM, event.item)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}