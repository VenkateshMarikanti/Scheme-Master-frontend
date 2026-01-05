package com.simats.schememasters.models

import com.google.gson.annotations.SerializedName

data class UserDocument(
    @SerializedName("document_id")
    val documentId: Int,
    @SerializedName("document_type")
    val documentType: String,
    @SerializedName("file_name")
    val fileName: String,
    val status: String,
    @SerializedName("uploaded_at")
    val uploadedAt: String
)