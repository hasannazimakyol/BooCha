package com.boocha.feature.profile.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.profile.BookListViewModel
import com.boocha.feature.profile.ChangeStatusEvent
import com.boocha.feature.profile.adapter.BookListAdapter
import com.boocha.feature.swapdetail.ui.SwapDetailActivity
import com.boocha.model.Swap
import com.boocha.util.*
import kotlinx.android.synthetic.main.fragment_book_list.*

class BookListFragment : BaseFragment() {

    lateinit var viewModel: BookListViewModel

    private val swapStatus: Int? by lazy { arguments?.getInt(BUNDLE_SWAP_STATUS) }
    private val bookList: ArrayList<Swap>? by lazy { arguments?.getParcelableArrayList<Swap>(BUNDLE_BOOK_LIST) }
    private val isCurrentUser: Boolean by lazy { arguments?.getBoolean(BUNDLE_IS_CURRENT_USER) as Boolean }

    companion object {
        private const val BUNDLE_SWAP_STATUS = "bundle_swap_status"
        private const val BUNDLE_BOOK_LIST = "bundle_book_list"
        private const val BUNDLE_IS_CURRENT_USER = "is_current_user"

        fun newInstance(swapStatus: Int, bookList: ArrayList<Swap>, currentUser: Boolean): BookListFragment {
            val bookListFragment = BookListFragment()
            val bundle = Bundle()

            bundle.putInt(BUNDLE_SWAP_STATUS, swapStatus)
            bundle.putParcelableArrayList(BUNDLE_BOOK_LIST, bookList)
            bundle.putBoolean(BUNDLE_IS_CURRENT_USER, currentUser)
            bookListFragment.arguments = bundle

            return bookListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(BookListViewModel::class.java)

        initBookListRecyclerView()
    }

    private fun initBookListRecyclerView() {
        val filteredList = bookList?.filter { it.swapStatus == swapStatus }

        if (filteredList != null) {
            rvBookList.setHasFixedSize(true)
            rvBookList.adapter = BookListAdapter().also { adapter ->
                adapter.bookList = filteredList
                adapter.isCurrentUser = isCurrentUser
                adapter.onClickListener = object : OnClickLister {
                    override fun itemOnClick(view: View, position: Int) {
                        if (view.id == R.id.ibMenu) {
                            changeSwapStatus(filteredList[position])

                        } else if (view.id == R.id.cardView) {
                            startActivity(SwapDetailActivity.newIntent(context!!, SwapDetailActivity.OPENED_FROM_HOME_ACTIVITY, filteredList[position]))
                        }
                    }
                }
            }
        }

        if (filteredList.isNullOrEmpty()) {
            noResultLayout.visibility = View.VISIBLE
        } else {
            noResultLayout.visibility = View.GONE
        }

    }

    private fun changeSwapStatus(swap: Swap) {
        when (swap.swapStatus) {
            SWAP_STATUS_ACTIVE -> {
                AlertDialog.Builder(context)
                        .setTitle("Change Swap Status")
                        .setMessage("You can tab to button for changing the swap status.")
                        .setNeutralButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("Remove") { dialog, _ ->
                            updateSwapStatus(swap, SWAP_STATUS_INACTIVE)
                            dialog.dismiss()
                        }
                        .setPositiveButton("Swapped") { dialog, _ ->
                            updateSwapStatus(swap, SWAP_STATUS_SWAPPED)
                            dialog.dismiss()
                        }
                        .show()
            }
            SWAP_STATUS_SWAPPED -> {
                AlertDialog.Builder(context)
                        .setTitle("Change Swap Status")
                        .setMessage("You can tab to button for changing the swap status.")
                        .setNeutralButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Remove") { dialog, _ ->
                            updateSwapStatus(swap, SWAP_STATUS_INACTIVE)
                            dialog.dismiss()
                        }
                        .show()
            }
            SWAP_STATUS_LIBRARY -> {
                AlertDialog.Builder(context)
                        .setTitle("Change Swap Status")
                        .setMessage("You can tab to button for changing the swap status.")
                        .setNeutralButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("Remove") { _, _ ->
                            updateSwapStatus(swap, SWAP_STATUS_INACTIVE)
                        }.setPositiveButton("Swappable") { _, _ ->
                            updateSwapStatus(swap, SWAP_STATUS_ACTIVE)
                        }
                        .show()

            }
        }
    }

    private fun updateSwapStatus(swap: Swap, swapStatus: Int) {
        viewModel.updateSwapStatus(swap.owner?.id!!, swap.id!!, swapStatus)
        postEvent(ChangeStatusEvent())
    }
}