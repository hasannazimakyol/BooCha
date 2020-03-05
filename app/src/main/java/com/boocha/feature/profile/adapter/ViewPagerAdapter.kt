package com.boocha.feature.profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.boocha.feature.profile.ui.BookListFragment
import com.boocha.model.Swap
import com.boocha.util.SWAP_STATUS_ACTIVE
import com.boocha.util.SWAP_STATUS_LIBRARY
import com.boocha.util.SWAP_STATUS_SWAPPED

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private var bookList: ArrayList<Swap> = ArrayList()
    private var isCurrentUser: Boolean = true

    fun setBookList(bookList: ArrayList<Swap>) {
        this.bookList = bookList
    }

    fun setIsCurrentUser(isCurrentUser: Boolean) {
        this.isCurrentUser = isCurrentUser
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                BookListFragment.newInstance(SWAP_STATUS_LIBRARY, bookList,isCurrentUser)
            }
            1 -> {
                BookListFragment.newInstance(SWAP_STATUS_ACTIVE, bookList,isCurrentUser)
            }
            2 -> {
                BookListFragment.newInstance(SWAP_STATUS_SWAPPED, bookList,isCurrentUser)
            }
            else -> {
                BookListFragment.newInstance(SWAP_STATUS_ACTIVE, bookList,isCurrentUser)
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Library"
            }
            1 -> {
                "Swappable"
            }
            2 -> {
                "Swapped"
            }
            else -> {
                return super.getPageTitle(position)
            }
        }
    }
}