package com.boocha.feature.swapdetail

import android.content.Context
import androidx.lifecycle.ViewModel
import com.boocha.model.User
import com.boocha.util.WriteObjectFile

class SwapDetailViewModel : ViewModel() {
    fun getSenderUser(context: Context) = WriteObjectFile(context).readObject(WriteObjectFile.FILE_USER) as User?
}