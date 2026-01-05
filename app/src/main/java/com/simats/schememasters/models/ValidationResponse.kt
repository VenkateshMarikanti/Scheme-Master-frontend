package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class ValidationResponse(
    val status: String,
    val validation: List<ValidationItem>? = null,
    val message: String? = null
)

data class ValidationItem(
    @SerializedName("document_name")
    val documentName: String,
    val status: String
)