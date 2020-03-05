package com.boocha.feature.swapdetail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.swapdetail.adapter.OwnerUsersAdapter
import com.boocha.feature.swapdetail.event.BookOwnerClickEvent
import com.boocha.model.Swap
import com.boocha.util.OnClickLister
import kotlinx.android.synthetic.main.fragment_book_owners.*

class BookOwnersFragment : BaseFragment() {

    private lateinit var swaps: ArrayList<Swap?>
    private lateinit var ownerUsersAdapter: OwnerUsersAdapter

    companion object {
        private const val BUNDLE_SWAPS = "bundle_swaps"

        fun newInstance(swaps: ArrayList<Swap?>?): BookOwnersFragment {
            val bookOwnersFragment = BookOwnersFragment()
            val bundle = Bundle()

            bundle.putParcelableArrayList(BUNDLE_SWAPS, swaps)
            bookOwnersFragment.arguments = bundle

            return bookOwnersFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_owners, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swaps = arguments?.getParcelableArrayList<Swap?>(BUNDLE_SWAPS) as ArrayList<Swap?>

        prepareUiWithSwaps()
        initRecyclerView()
    }

    private fun prepareUiWithSwaps() {
        val book = swaps[0]?.book

        tvBookName.text = book?.title
        tvBookDescription.text = book?.description ?: getString(R.string.no_description_available)
        tvAuthorName.text = getString(R.string.author_variable, book?.author)
        tvBookPublisher.text = getString(R.string.publisher_variable, book?.publisher)
        tvPageCount.text = getString(R.string.page_variable, book?.pageCount)
        tvPublishDate.text = getString(R.string.date_variable, book?.publishedDate)
    }

    private fun initRecyclerView() {
        ownerUsersAdapter = OwnerUsersAdapter()
        ownerUsersAdapter.swaps = swaps
        ownerUsersAdapter.onClickLister = object : OnClickLister {
            override fun itemOnClick(view: View, position: Int) {
                postEvent(BookOwnerClickEvent(swaps[position]))
            }
        }

        rvOwnerUsers.apply {
            setHasFixedSize(true)
            adapter = ownerUsersAdapter
        }


    }
}