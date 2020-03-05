package com.boocha.feature.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.data.remote.util.Status
import com.boocha.feature.search.BookItemClickEvent
import com.boocha.feature.search.adapter.SearchAdapter
import com.boocha.feature.search.viewmodel.SearchFragmentViewModel
import com.boocha.model.book.Item
import com.boocha.util.OnClickLister
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

    private lateinit var viewModel: SearchFragmentViewModel
    private lateinit var searchAdapter: SearchAdapter
    private var bookList: List<Item?> = ArrayList()

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SearchFragmentViewModel::class.java)

        initRecyclerView()
        initOnClickListener()
        focusOnSearchBar()
        initSearchBookLiveData()
    }

    private fun focusOnSearchBar() {
        etSearch.requestFocus()
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(etSearch, InputMethodManager.SHOW_FORCED)
    }

    private fun initOnClickListener() {

        ivClose.setOnClickListener {
            activity?.onBackPressed()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                viewModel.searchBook(text?.toString() ?: "")
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun initRecyclerView() {
        searchAdapter = SearchAdapter()
        searchAdapter.onClickLister = object : OnClickLister {
            override fun itemOnClick(view: View, position: Int) {
                bookList[position]?.let { item ->
                    postEvent(BookItemClickEvent(item))
                }
            }
        }

        rvSearchList.apply {
            setHasFixedSize(true)
            adapter = searchAdapter
        }
    }

    private fun initSearchBookLiveData() {
        viewModel.searchBookLiveData.observe(this, Observer { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    bookList = response.data?.items ?: ArrayList()
                    if (bookList.isNotEmpty()) {
                        searchAdapter.bookList = bookList
                        searchAdapter.notifyDataSetChanged()
                    }
                }
                Status.ERROR -> {
                }
                Status.LOADING -> {
                }
            }
        })
    }
}