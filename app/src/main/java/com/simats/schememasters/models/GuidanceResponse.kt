package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class GuidanceResponse(
    val status: String,
    val document: String,
    @SerializedName("guidance_steps")
    val guidanceSteps: List<GuidanceStep>
)

data class GuidanceStep(
    @SerializedName("step_number")
    val stepNumber: Int,
    @SerializedName("step_description")
    val stepDescription: String
)