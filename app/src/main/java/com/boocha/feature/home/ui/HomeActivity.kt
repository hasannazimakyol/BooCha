package com.boocha.feature.home.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseActivity
import com.boocha.feature.home.event.SuccessAddSwap
import com.boocha.feature.home.event.UnreadMessageCountEvent
import com.boocha.feature.home.viewmodel.HomeActivityViewModel
import com.boocha.model.User
import com.boocha.util.WriteObjectFile
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeActivity : BaseActivity() {

    lateinit var viewModel: HomeActivityViewModel

    private var currentUser: User? = null

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)

        initUserLiveData()
        initUnreadMessagesCountLiveData()
        initOnClickListener()
        navigateToHomeFragment()

        viewModel.updateLastLogin()
        viewModel.getCurrentUser()
    }

    override fun onStart() {
        super.onStart()
        registerToEvent()
    }

    override fun onResume() {
        super.onResume()
        currentUser?.let { viewModel.messages(it) }
    }

    override fun onStop() {
        super.onStop()
        unregisterToEvent()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.are_you_sure_you_want_to_exit))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun initOnClickListener() {
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    if (navigation.selectedItemId != R.id.action_home) {
                        navigateToHomeFragment()
                        true
                    } else {
                        false
                    }

                }
                R.id.action_book -> {
                    if (navigation.selectedItemId != R.id.action_book) {
                        navigateToSearchFragment()
                        true
                    } else {
                        false
                    }
                }
                R.id.action_profile -> {
                    if (navigation.selectedItemId != R.id.action_profile) {
                        navigateToProfileFragment()
                        true
                    } else {
                        false
                    }
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun initUserLiveData() {
        val writeObjectFile = WriteObjectFile(this)

        viewModel.userLiveData.observe(this, Observer {
            it.data?.let { user ->
                currentUser = user
                writeObjectFile.writeObject(user, WriteObjectFile.FILE_USER)
                viewModel.messages(user)
            }
        })
    }

    private fun initUnreadMessagesCountLiveData() {
        viewModel.unreadMessagesCountLiveData.observe(this, Observer { resource ->
            EventBus.getDefault().post(resource.data?.let { UnreadMessageCountEvent(it) })
        })
    }

    private fun navigateToHomeFragment() {
        replaceFragment(R.id.fragmentHolder, HomeFragment.newInstance(), HomeFragment.TAG, false)
    }

    private fun navigateToSearchFragment() {
        replaceFragment(R.id.fragmentHolder, AddBookForSwapFragment.newInstance(), AddBookForSwapFragment.TAG, false)
    }

    private fun navigateToProfileFragment() {
        replaceFragment(
                R.id.fragmentHolder,
                ProfileFragment.newInstance(viewModel.getCurrentFirebaseUser()?.uid),
                ProfileFragment.TAG,
                false
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccessAddSwap(event: SuccessAddSwap) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        navigation.selectedItemId = R.id.action_home
    }
}