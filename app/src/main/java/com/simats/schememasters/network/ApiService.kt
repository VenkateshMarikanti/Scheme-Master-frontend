package com.simats.schememasters.network

import com.simats.schememasters.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("forgot_password.php")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("reset_password.php")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<RegisterResponse>

    // Google Sign In
    @POST("google_login.php")
    fun googleLogin(@Body request: GoogleLoginRequest): Call<GoogleLoginResponse>

    // Profile Operations
    @GET("get_profile.php")
    fun getProfile(@Query("user_id") userId: Int): Call<ProfileResponse>

    @FormUrlEncoded
    @POST("update_profile.php")
    fun updateProfile(
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("caste") caste: String
    ): Call<RegisterResponse>

    // Farmer Schemes Operations
    @FormUrlEncoded
    @POST("add_farmer_scheme.php")
    fun addFarmerScheme(
        @Field("scheme_name") schemeName: String,
        @Field("aadhar_name") aadharName: String,
        @Field("land_type") landType: String,
        @Field("eligibility_criteria") eligibilityCriteria: String,
        @Field("specifications") specifications: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("update_farmer_scheme.php")
    fun updateFarmerScheme(
        @Field("id") id: Int,
        @Field("scheme_name") schemeName: String,
        @Field("aadhar_name") aadharName: String,
        @Field("land_type") landType: String,
        @Field("eligibility_criteria") eligibilityCriteria: String,
        @Field("specifications") specifications: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("delete_farmer_scheme.php")
    fun deleteFarmerScheme(
        @Field("id") id: Int
    ): Call<RegisterResponse>

    @GET("get_farmer_schemes.php")
    fun getFarmerSchemes(@Query("search") search: String = "", @Query("caste") caste: String = ""): Call<FarmerSchemesResponse>

    // Student Schemes Operations
    @FormUrlEncoded
    @POST("add_student_scheme.php")
    fun addStudentScheme(
        @Field("scheme_name") schemeName: String,
        @Field("aadhar_name") aadharName: String,
        @Field("caste_name") casteName: String,
        @Field("eligibility_criteria") eligibilityCriteria: String,
        @Field("specifications") specifications: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("update_student_scheme.php")
    fun updateStudentScheme(
        @Field("id") id: Int,
        @Field("scheme_name") schemeName: String,
        @Field("aadhar_name") aadharName: String,
        @Field("caste_name") casteName: String,
        @Field("eligibility_criteria") eligibilityCriteria: String,
        @Field("specifications") specifications: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("delete_student_scheme.php")
    fun deleteStudentScheme(
        @Field("id") id: Int
    ): Call<RegisterResponse>

    @GET("get_student_schemes.php")
    fun getStudentSchemes(@Query("search") search: String = "", @Query("caste") caste: String = ""): Call<StudentSchemesResponse>

    // Document Operations
    @Multipart
    @POST("upload_document.php")
    fun uploadDocument(
        @Part("user_id") userId: RequestBody,
        @Part("document_type") documentType: RequestBody,
        @Part document: MultipartBody.Part
    ): Call<RegisterResponse>

    @GET("get_documents.php")
    fun getDocuments(
        @Query("user_id") userId: Int
    ): Call<UserDocumentsResponse>

    @FormUrlEncoded
    @POST("delete_document.php")
    fun deleteDocument(
        @Field("document_id") documentId: Int
    ): Call<RegisterResponse>

    // Document Validation
    @FormUrlEncoded
    @POST("validate_documents.php")
    fun validateDocuments(
        @Field("user_id") userId: Int,
        @Field("scheme_id") schemeId: Int
    ): Call<ValidationResponse>

    // Document Guidance
    @FormUrlEncoded
    @POST("get_document_guidance.php")
    fun getDocumentGuidance(
        @Field("document_name") documentName: String
    ): Call<GuidanceResponse>
}
