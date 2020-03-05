package com.boocha.feature.home.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.data.remote.util.Status
import com.boocha.feature.home.viewmodel.ProfileFragmentViewModel
import com.boocha.feature.login.ui.LoginActivity
import com.boocha.feature.profile.ChangeStatusEvent
import com.boocha.feature.profile.adapter.ViewPagerAdapter
import com.boocha.model.Swap
import com.boocha.model.User
import com.boocha.util.PhotoOrientationUtil
import com.boocha.util.SWAP_STATUS_ACTIVE
import com.boocha.util.SWAP_STATUS_SWAPPED
import com.boocha.util.WriteObjectFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_profile2.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException


class ProfileFragment : BaseFragment() {

    lateinit var viewModel: ProfileFragmentViewModel
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var writeObjectFile: WriteObjectFile

    private var lastPosition = 0;

    val id: String by lazy { arguments?.getString(BUNDLE_ID) as String }

    companion object {

        const val TAG = "profile_fragment"

        private const val BUNDLE_ID = "id"

        private const val REQUEST_PICK_PHOTO = 100

        fun newInstance(id: String? = ""): ProfileFragment {
            val profileFragment = ProfileFragment()
            val bundle = Bundle()

            bundle.putString(BUNDLE_ID, id)
            profileFragment.arguments = bundle

            return profileFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ProfileFragmentViewModel::class.java)
        viewPagerAdapter = ViewPagerAdapter(activity?.supportFragmentManager!!)
        context?.let {
            writeObjectFile = WriteObjectFile(it)
        }

        initOnClickListener()
        initUserLiveData()
        initSwapsLiveData()
        initUpdateProfilePhotoLiveData()

        viewModel.getUser(id)
        viewModel.getUserSwaps(id)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val image = getPath(data?.data!!)
                    val imageFile = resizePhoto(image!!, 0, context!!)

                    viewModel.updateProfilePhoto(id, imageFile)
                }
            }
        }
    }

    private fun initOnClickListener() {
        btnSignOut.setOnClickListener {
            context?.let { context ->
                AlertDialog.Builder(context)
                        .setTitle(getString(R.string.sign_out))
                        .setMessage(getString(R.string.are_you_sure_you_want_to_sign_out))
                        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.signOut()
                            writeObjectFile.deleteObject(WriteObjectFile.FILE_USER)
                            startActivity(LoginActivity.newIntent(context))
                            activity?.finish()
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                        .show()

            }
        }

        ivProfilePhoto.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun initUserLiveData() {
        viewModel.userLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    updateUiWithUser(resource.data)

                    if (resource.data?.id == viewModel.getCurrentUserAccount()?.uid) {
                        writeObjectFile.writeObject(resource.data!!, WriteObjectFile.FILE_USER)
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


    private fun initSwapsLiveData() {
        viewModel.swapsLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()
                    val bookList = resource.data

                    if (bookList != null) {
                        viewPagerAdapter.setBookList(resource.data)
                        viewPagerAdapter.setIsCurrentUser((id != viewModel.getCurrentUserAccount()?.uid))
                        viewPager.adapter = viewPagerAdapter
                        viewPagerAdapter.notifyDataSetChanged()
                        updateUiWithSwaps(bookList)
                        viewPager.setCurrentItem(lastPosition, false)
                    }

                }
                Status.ERROR -> {
                    showErrorDialog(resource.message)
                }
                Status.LOADING -> {
                }
            }
        })

    }

    private fun initUpdateProfilePhotoLiveData() {
        viewModel.updateProfilePhotoLiveData.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let {
                        dismissLoadingDialog()
                        Glide.with(context!!).load(it).into(ivProfilePhoto)
                    }
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

    private fun updateUiWithUser(user: User?) {
        tvUsername.text = "${user?.name} ${user?.surname}"

        if (user?.profilePhoto.isNullOrEmpty().not()) {
            Glide.with(this)
                    .load(user?.profilePhoto)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(ivProfilePhoto)
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_add_photo)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(ivProfilePhoto)
        }

        if (id != viewModel.getCurrentUserAccount()?.uid) {
            ivProfilePhoto.setOnClickListener(null)
            btnSignOut.visibility = View.GONE
        }
    }

    private fun updateUiWithSwaps(swaps: ArrayList<Swap>) {
        tvSwappableCount.text = (swaps.filter { it.swapStatus == SWAP_STATUS_ACTIVE }.size).toString()
        tvSwappedCount.text = (swaps.filter { it.swapStatus == SWAP_STATUS_SWAPPED }.size).toString()
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_PHOTO)
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

    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver!!.query(uri, projection, null, null, null)
                ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeStatusEvent(event: ChangeStatusEvent) {
        showLoadingDialog()
        lastPosition = viewPager.currentItem
        viewModel.getUserSwaps(id)
    }
}