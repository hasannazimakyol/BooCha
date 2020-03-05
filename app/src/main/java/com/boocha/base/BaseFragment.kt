package com.boocha.base

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.boocha.R
import org.greenrobot.eventbus.EventBus

open class BaseFragment : Fragment() {

    private var alertDialog: AlertDialog? = null

    fun hideToolbar() {
        (activity as AppCompatActivity).supportActionBar?.customView?.animate()?.alpha(0.0f)?.duration = 300
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    fun showToolbar() {
        (activity as AppCompatActivity).supportActionBar?.customView?.animate()?.alpha(1.0f)?.duration = 300
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    fun showLoadingDialog() {
        context?.let {
            alertDialog = AlertDialog.Builder(it)
                    .setView(R.layout.dialog_loading)
                    .setCancelable(false)
                    .show()
        }
    }

    fun dismissLoadingDialog() {
        alertDialog?.dismiss()
    }

    fun showErrorDialog(message: String?) {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(getString(R.string.error))
                    .setMessage(message ?: getString(R.string.generic_error_message))
                    .setPositiveButton(getText(R.string.ok)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setCancelable(true)
                    .show()
        }
    }


    fun postEvent(event: Any) {
        EventBus.getDefault().post(event)
    }
}