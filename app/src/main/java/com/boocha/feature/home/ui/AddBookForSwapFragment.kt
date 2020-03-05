package com.boocha.feature.home.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.data.remote.util.Status
import com.boocha.feature.home.event.SuccessAddSwap
import com.boocha.feature.home.viewmodel.AddBookForSwapFragmentViewModel
import com.boocha.feature.search.ui.SearchActivity
import com.boocha.model.Book
import com.boocha.model.Swap
import com.boocha.model.User
import com.boocha.model.book.Item
import com.boocha.util.*
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import kotlinx.android.synthetic.main.fragment_add_book_for_swap.*
import java.io.File
import java.io.IOException


class AddBookForSwapFragment : BaseFragment() {

    lateinit var viewModel: AddBookForSwapFragmentViewModel
    lateinit var writeObjectFile: WriteObjectFile

    private var swap: Swap = Swap()
    private var image: File? = null

    companion object {

        const val TAG = "add_book_for_swap_fragment"

        private const val REQUEST_CODE_SEARCH_ACTIVITY_START = 100
        private const val REQUEST_CODE_SELECT_PHOTO = 553


        fun newInstance(): AddBookForSwapFragment {
            return AddBookForSwapFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_book_for_swap, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AddBookForSwapFragmentViewModel::class.java)

        context?.let {
            writeObjectFile = WriteObjectFile(it)
        }

        initAddSwapLiveData()
        initOnClickListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SEARCH_ACTIVITY_START -> {
                if (resultCode == Activity.RESULT_OK) {
                    tvHelper.visibility = View.GONE

                    val item = data?.getParcelableExtra(SearchActivity.EXTRA_BOOK_ITEM) as Item

                    swap.book = itemMapperToBook(item)

                    updateUiWithBookItem(item)

                } else {
                    tvHelper.visibility = View.VISIBLE
                }
            }

            REQUEST_CODE_SELECT_PHOTO -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    ivBook.setImageDrawable(resources.getDrawable(R.drawable.ic_add_photo))
                }
            }
        }

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val selectedImage = ImagePicker.getFirstImageOrNull(data)
            if (selectedImage != null) {
                ivBook.setBackgroundColor(Color.WHITE)
                Glide.with(this).load(selectedImage.path).into(ivBook)

                image = resizePhoto(selectedImage.path, 0, context!!)
            } else {
                ivBook.setImageDrawable(resources.getDrawable(R.drawable.ic_add_photo))
                image = null
            }
        }
    }

    private fun initOnClickListener() {
        tvHelper.setOnClickListener {
            navigateToSearchActivity()
        }

        ivBook.setOnClickListener {
            selectPhoto()
        }

        btnAdd.setOnClickListener {

            when {
                swap.book == null -> {
                    Toast.makeText(context, "Please select a book.", Toast.LENGTH_LONG).show()
                }
                image == null -> {
                    Toast.makeText(context, "Please add a book photo.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    swap.owner = writeObjectFile.readObject(WriteObjectFile.FILE_USER) as User?
                    swap.ownerDescription = etOwnerDescription.text.toString()
                    swap.date = getCurrentTime()
                    swap.bookStatus = getBookStatus()
                    swap.swapStatus = getSwapStatus()
                    swap.whereGetStatus = getWhereGetStatus()

                    image?.let {
                        viewModel.addSwap(it, swap)
                    }
                }
            }
        }

        rgAddFor.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.rbAddForLibrary) {
                tvWhereDidYouGet.visibility = View.VISIBLE
                cvWhereDidYouGet.visibility = View.VISIBLE
            } else {
                tvWhereDidYouGet.visibility = View.GONE
                cvWhereDidYouGet.visibility = View.GONE
            }
        }
    }

    private fun initAddSwapLiveData() {
        viewModel.addSwapLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    postEvent(SuccessAddSwap())
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                    showErrorDialog(resource.message)
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun navigateToSearchActivity() {
        context?.let {
            startActivityForResult(
                    SearchActivity.newIntent(it, SearchActivity.OPENED_FROM_ADD_BOOK_FOR_SWAP_FRAGMENT),
                    REQUEST_CODE_SEARCH_ACTIVITY_START
            )
        }
    }

    private fun selectPhoto() {
        ImagePicker.cameraOnly().start(this)
    }

    private fun updateUiWithBookItem(item: Item) {
        tvBookName.setBackgroundColor(Color.WHITE)
        tvAuthorName.setBackgroundColor(Color.WHITE)
        tvBookDescription.setBackgroundColor(Color.WHITE)
        tvBookPublisher.setBackgroundColor(Color.WHITE)

        tvBookName.text = item.volumeInfo?.title ?: ""
        tvAuthorName.text = item.volumeInfo?.authors?.get(0) ?: ""
        tvBookDescription.text = item.volumeInfo?.description ?: ""
        item.volumeInfo?.publisher?.let {
            tvBookPublisher.text = getString(R.string.publisher_variable, it)
        }
    }

    private fun itemMapperToBook(item: Item): Book {
        val id = item.id ?: ""
        val title = item.volumeInfo?.title ?: ""
        val author = item.volumeInfo?.authors?.get(0) ?: ""
        val description = item.volumeInfo?.description ?: ""
        val publisher = item.volumeInfo?.publisher ?: ""
        val publishedDate = item.volumeInfo?.publishedDate ?: ""
        val isbn13 = item.volumeInfo?.industryIdentifiers?.get(1)?.identifier ?: ""
        val language = item.volumeInfo?.language ?: ""
        val pageCount = item.volumeInfo?.pageCount.toString()

        return Book(id, title, author, description, publisher, publishedDate, isbn13, language, pageCount)
    }

    private fun getBookStatus(): Int {
        return when (rgBookStatus.checkedRadioButtonId) {
            R.id.rbStatusNotGood -> {
                BOOK_STATUS_NOT_GOOD
            }
            R.id.rbStatusGood -> {
                BOOK_STATUS_GOOD
            }
            R.id.rbStatusPerfect -> {
                BOOK_STATUS_PERFECT
            }
            else -> {
                BOOK_STATUS_GOOD
            }

        }
    }

    private fun getSwapStatus(): Int {
        return when (rgAddFor.checkedRadioButtonId) {
            R.id.rbAddForSwap -> {
                SWAP_STATUS_ACTIVE
            }
            R.id.rbAddForLibrary -> {
                SWAP_STATUS_LIBRARY
            }
            else -> {
                SWAP_STATUS_ACTIVE
            }
        }
    }

    private fun getWhereGetStatus(): Int {
        if (cvWhereDidYouGet.visibility == View.GONE) {
            return WHERE_DID_YOU_GET_STATUS_FROM_OTHERS
        }

        return when (rgWhereDidYouGet.checkedRadioButtonId) {
            R.id.rbFromOthers -> {
                WHERE_DID_YOU_GET_STATUS_FROM_OTHERS
            }
            R.id.rbFromBooCha -> {
                WHERE_DID_YOU_GET_STATUS_FROM_BOOCHA
            }
            else -> {
                SWAP_STATUS_ACTIVE
            }
        }
    }

    private fun resizePhoto(path: String, angle: Int, context: Context): File {
        val truePath = if (path.startsWith("file://")) {
            path.substring(7)
        } else {
            path
        }
        val imageFile = File(truePath)
        try {
            val uri = Uri.fromFile(imageFile)
            var bmp = PhotoOrientationUtil.modifyOrientation(MediaStore.Images.Media.getBitmap(context.contentResolver, uri), truePath)
            if (angle != 0) {
                bmp = PhotoOrientationUtil.rotate(bmp, angle.toFloat())
            }
            val fOut = context.contentResolver.openOutputStream(uri)
            val dstWidth: Double
            val dstHeight: Double
            if (bmp.width > bmp.height) {
                dstWidth = if (bmp.width > 1920.0) {
                    1920.0
                } else {
                    bmp.width.toDouble()
                }
                val scaleFactor = bmp.height.toDouble() / bmp.width.toDouble()
                dstHeight = dstWidth * scaleFactor
            } else {
                dstHeight = if (bmp.height > 1920.0) {
                    1920.0
                } else {
                    bmp.height.toDouble()
                }
                val scaleFactor = bmp.width.toDouble() / bmp.height.toDouble()
                dstWidth = dstHeight * scaleFactor
            }

            bmp = Bitmap.createScaledBitmap(bmp, dstWidth.toInt(), dstHeight.toInt(), false)
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, fOut)
            fOut?.flush()
            fOut?.close()
            return imageFile
        } catch (e: IOException) {
            return imageFile
        } catch (e: NullPointerException) {
            return imageFile
        }
    }
}