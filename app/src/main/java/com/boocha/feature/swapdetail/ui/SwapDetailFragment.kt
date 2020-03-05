package com.boocha.feature.swapdetail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.messages.event.UserClickEvent
import com.boocha.feature.swapdetail.SwapDetailViewModel
import com.boocha.feature.swapdetail.event.SendSwapRequestClickEvent
import com.boocha.model.Swap
import com.boocha.util.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_swap_detail.*

class SwapDetailFragment : BaseFragment() {

    private lateinit var swap: Swap
    private lateinit var viewModel: SwapDetailViewModel

    companion object {
        private const val BUNDLE_SWAP = "bundle_swap"

        fun newInstance(swap: Swap): SwapDetailFragment {
            val swapDetailFragment = SwapDetailFragment()
            val bundle = Bundle()

            bundle.putParcelable(BUNDLE_SWAP, swap)
            swapDetailFragment.arguments = bundle

            return swapDetailFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_swap_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SwapDetailViewModel::class.java)

        arguments?.getParcelable<Swap>(BUNDLE_SWAP)?.let {
            swap = it
        }

        initOnClickListener()
        prepareUiWithSwap()
    }

    private fun initOnClickListener() {
        btnSendSwapRequest.setOnClickListener {
            postEvent(SendSwapRequestClickEvent())
        }

        layoutSwapOwner.setOnClickListener {
            postEvent(UserClickEvent(swap.owner?.id!!))
        }
    }

    private fun prepareUiWithSwap() {
        tvBookName.text = swap.book?.title
        if (swap.book?.description.isNullOrEmpty()) {
            tvBookDescription.text = getString(R.string.no_description_available)
        } else {
            tvBookDescription.text = swap.book?.description
        }
        tvAuthorName.text = getString(R.string.author_variable, swap.book?.author)
        tvBookPublisher.text = getString(R.string.publisher_variable, swap.book?.publisher)
        tvPageCount.text = getString(R.string.page_variable, swap.book?.pageCount)
        tvPublishDate.text = getString(R.string.date_variable, swap.book?.publishedDate)

        if (swap.ownerDescription.isNullOrEmpty()) {
            tvOwnerDescription.visibility = View.GONE
        } else {
            tvOwnerDescription.visibility = View.VISIBLE
            tvOwnerDescription.text = swap.ownerDescription
        }

        when (swap.bookStatus) {
            BOOK_STATUS_NOT_GOOD -> {
                tvBookStatus.text = getString(R.string.book_status_variable, getString(R.string.not_good))
            }
            BOOK_STATUS_GOOD -> {
                tvBookStatus.text = getString(R.string.book_status_variable, getString(R.string.good))
            }
            BOOK_STATUS_PERFECT -> {
                tvBookStatus.text = getString(R.string.book_status_variable, getString(R.string.perfect))
            }
            else -> {
                getString(R.string.good)
            }
        }

        context?.let {
            Glide.with(it).load(swap.imageUri).into(ivBook)
        }

        if ((swap.owner?.profilePhoto.isNullOrEmpty().not())) {
            context?.let {
                Glide.with(it).load(swap.owner?.profilePhoto).into(ivUser)
            }
        } else {
            ivUser.visibility = View.GONE
        }

        tvUsername.text = "${swap.owner?.name} ${swap.owner?.surname}"
        tvDate.text = dateDifference(swap.date ?: "")

        if ((swap.owner?.id == viewModel.getSenderUser(context!!)?.id) || (swap.swapStatus != SWAP_STATUS_ACTIVE)) {
            btnSendSwapRequest.visibility = View.GONE
        }
    }
}