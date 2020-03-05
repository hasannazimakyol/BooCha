package com.boocha.feature.login.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boocha.R
import com.boocha.base.BaseActivity
import com.boocha.data.remote.util.Status
import com.boocha.feature.home.ui.HomeActivity
import com.boocha.feature.login.event.CreateNewAccountEvent
import com.boocha.feature.login.event.LoginEvent
import com.boocha.feature.login.event.SignUpEvent
import com.boocha.feature.login.viewmodel.LoginActivityViewModel
import kotlinx.android.synthetic.main.default_toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoginActivity : BaseActivity() {

    lateinit var viewModel: LoginActivityViewModel

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this).get(LoginActivityViewModel::class.java)

        initToolbar()
        initSignUpLiveData()
        initLoginLiveData()

        if (isUserLogged()) {
            startActivity(HomeActivity.newIntent(this))
            finish()
        } else {
            navigateToLoginFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        registerToEvent()
    }

    override fun onStop() {
        super.onStop()
        unregisterToEvent()
    }

    private fun initToolbar() {
        setSupportActionBar(defaultToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initSignUpLiveData() {
        viewModel.signUpLiveData.observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    dismissLoadingDialog()

                    AlertDialog.Builder(this)
                            .setTitle(getString(R.string.welcome))
                            .setMessage(getString(R.string.your_account_has_been_created_successfully))
                            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                supportFragmentManager.popBackStack()
                            }
                            .show()
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                    showErrorDialog(result.message)
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun initLoginLiveData() {
        viewModel.loginLiveData.observe(this, Observer { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    navigateToHomeActivity()
                    dismissLoadingDialog()
                }
                Status.ERROR -> {
                    dismissLoadingDialog()
                    showErrorDialog(result.message)
                }
                Status.LOADING -> {
                    showLoadingDialog()
                }
            }
        })
    }

    private fun navigateToLoginFragment() {
        replaceFragment(R.id.fragmentHolder, LoginFragment.newInstance(), animation = false)
    }

    private fun navigateToSignUpFragment() {
        replaceFragment(R.id.fragmentHolder, SignUpFragment.newInstance(), SignUpFragment.TAG, animation = true)
    }

    private fun navigateToHomeActivity() {
        startActivity(HomeActivity.newIntent(this))
        finish()
    }

    private fun isUserLogged(): Boolean {
        return viewModel.getCurrentUser() != null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCreateNewAccountEvent(event: CreateNewAccountEvent) {
        navigateToSignUpFragment()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSignUpEvent(event: SignUpEvent) {
        viewModel.signUp(event.user)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        viewModel.login(event.email, event.password)
    }
}