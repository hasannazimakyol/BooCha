package com.boocha.feature.login.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.boocha.R
import com.boocha.base.BaseFragment
import com.boocha.feature.login.event.SignUpEvent
import com.boocha.model.User
import kotlinx.android.synthetic.main.default_toolbar.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment() {

    companion object {
        const val TAG = "sign_up_fragment"

        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOnClickListener()
    }

    private fun initToolbar() {
        showToolbar()
        tvDefaultToolbarTitle?.text = getString(R.string.sign_up)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initOnClickListener() {
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                name.isBlank() -> {
                    etName.error = "Please fill in this field."
                }
                surname.isBlank() -> {
                    etSurname.error = "Please fill in this field."
                }
                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    etEmail.error = "Please enter a valid value."
                }
                password.length < 6 -> {
                    etPassword.error = "Password should be minimum 6 character."
                }
                else -> {
                    postEvent(SignUpEvent(User(name = name, surname = surname, email = email, password = password)))
                }
            }
        }
    }
}