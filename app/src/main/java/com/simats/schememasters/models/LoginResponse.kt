package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserData? = null
)

data class UserData(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val email: String
)
