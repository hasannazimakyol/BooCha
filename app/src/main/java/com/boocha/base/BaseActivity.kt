package com.boocha.base

import android.os.Build
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.boocha.R
import org.greenrobot.eventbus.EventBus

open class BaseActivity : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null

    fun replaceFragment(@IdRes containerView: Int, fragment: Fragment, backStackName: String? = null, animation: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            if (animation) {
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
            }
            if (backStackName != null) {
                addToBackStack(backStackName)
            }
            replace(containerView, fragment)
            commit()
        }
    }

    fun setStatusBarColor(@ColorRes color: Int) {
        val window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }

    fun registerToEvent() {
        EventBus.getDefault().register(this)
    }

    fun unregisterToEvent() {
        EventBus.getDefault().unregister(this)
    }

    fun showLoadingDialog() {
        alertDialog = AlertDialog.Builder(this)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .show()
    }

    fun dismissLoadingDialog() {
        alertDialog?.dismiss()
    }

    fun showErrorDialog(message: String?) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(message ?: getString(R.string.generic_error_message))
                .setPositiveButton(getText(R.string.ok)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setCancelable(true)
                .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}