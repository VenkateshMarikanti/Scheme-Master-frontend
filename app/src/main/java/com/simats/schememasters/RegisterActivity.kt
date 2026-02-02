package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.simats.schememasters.models.RegisterRequest
import com.simats.schememasters.models.RegisterResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val tvShowHide = findViewById<TextView>(R.id.tvShowHide)

        tvShowHide.setOnClickListener {
            if (isPasswordVisible) {
                // Hide Password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                tvShowHide.text = "Show"
            } else {
                // Show Password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                tvShowHide.text = "Hide"
            }
            isPasswordVisible = !isPasswordVisible
            // Move cursor to the end
            etPassword.setSelection(etPassword.text.length)
        }

        btnSignUp.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validation: Name contains only alphabetics (and spaces)
            if (!name.all { it.isLetter() || it.isWhitespace() }) {
                Toast.makeText(this, "Name should contain only alphabets", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validation: Email ends with @gmail.com
            if (!email.endsWith("@gmail.com") || email.length <= 10) {
                Toast.makeText(this, "Please enter a valid Gmail address (e.g. user@gmail.com)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validation: Password 8 chars, 1 uppercase, 1 digit
            val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
            if (!passwordRegex.matches(password)) {
                Toast.makeText(this, "Password must be at least 8 characters with one uppercase letter and one digit", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        tvSignIn.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val request = RegisterRequest(name, email, password)
        
        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_LONG).show()
                    
                    if (registerResponse.status == "success") {
                        // Registration successful, go to Login
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
