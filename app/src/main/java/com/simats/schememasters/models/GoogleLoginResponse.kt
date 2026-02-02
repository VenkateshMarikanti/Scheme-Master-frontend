package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class          GoogleLoginResponse(
    val status: String,
    @SerializedName("user_id")
    val userId: Int?,
    val message: String
)