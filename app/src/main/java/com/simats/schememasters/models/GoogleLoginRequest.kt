package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class GoogleLoginRequest(
    val email: String,
    val name: String,
    @SerializedName("google_id")
    val googleId: String
)