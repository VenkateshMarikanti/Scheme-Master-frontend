package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class FarmerScheme(
    val id: Int,
    @SerializedName("scheme_name")
    val schemeName: String,
    @SerializedName("aadhar_name")
    val aadharName: String,
    @SerializedName("land_type")
    val landType: String,
    @SerializedName("eligibility_criteria")
    val eligibilityCriteria: String,
    val specifications: String
)