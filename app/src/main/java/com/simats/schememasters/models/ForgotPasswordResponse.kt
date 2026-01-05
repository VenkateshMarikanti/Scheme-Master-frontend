package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    @SerializedName("reset_token")
    val resetToken: String? = null
)