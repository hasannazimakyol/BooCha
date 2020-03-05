package com.boocha.util

import android.view.View

interface OnClickLister {
    fun itemOnClick(view: View, position: Int)
}