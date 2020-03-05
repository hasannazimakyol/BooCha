package com.boocha.feature.login.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.login.event.CreateNewAccountEvent
import com.boocha.feature.login.event.LoginEvent
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideToolbar()
        initOnClickListener()
    }

    private fun initOnClickListener() {

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    etEmail.error = "Please enter a valid value."
                }
                password.length < 6 -> {
                    etPassword.error = "Password should be minimum 6 character."
                }
                else -> {
                    postEvent(LoginEvent(email, password))
                }
            }
        }


        tvCreateNewAccount.setOnClickListener {
            postEvent(CreateNewAccountEvent())
        }
    }
}