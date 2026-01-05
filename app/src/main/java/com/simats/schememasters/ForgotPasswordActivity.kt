package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.simats.schememasters.models.ForgotPasswordRequest
import com.simats.schememasters.models.ForgotPasswordResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etForgotEmail = findViewById<EditText>(R.id.etForgotEmail)
        val btnSendReset = findViewById<MaterialButton>(R.id.btnSendReset)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        btnSendReset.setOnClickListener {
            val email = etForgotEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requestPasswordReset(email)
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun requestPasswordReset(email: String) {
        val request = ForgotPasswordRequest(email)

        RetrofitClient.instance.forgotPassword(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val forgotResponse = response.body()!!
                    
                    if (forgotResponse.status == "success") {
                        // Display the message (Success or Email Failed)
                        Toast.makeText(this@ForgotPasswordActivity, forgotResponse.message, Toast.LENGTH_LONG).show()
                        
                        // Navigate to ResetPasswordActivity
                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        
                        // We pass the token automatically from the API response
                        // This allows the user to reset even if the email wasn't sent
                        intent.putExtra("RESET_TOKEN", forgotResponse.resetToken)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, forgotResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}