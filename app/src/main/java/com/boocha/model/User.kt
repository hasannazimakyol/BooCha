package com.boocha.model

import java.io.Serializable

data class User(
        var id: String = "",
        val name: String = "",
        val surname: String = "",
        val profilePhoto: String = "",
        val email: String = "",
        val password: String = "",
        val lastLogin: String = ""
) : Serializable