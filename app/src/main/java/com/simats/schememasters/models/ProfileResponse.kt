package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val status: String,
    val data: ProfileData?,
    val message: String?
)

data class ProfileData(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val caste: String?
)