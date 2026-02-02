package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContinueActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continue)

        val name = intent.getStringExtra("USER_NAME") ?: "User"
        val email = intent.getStringExtra("USER_EMAIL") ?: ""

        val tvWelcomeText = findViewById<TextView>(R.id.tvWelcomeText)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val tvSwitchAccount = findViewById<TextView>(R.id.tvSwitchAccount)

        tvWelcomeText.text = "Continue as $name"
        tvUserEmail.text = email

        btnContinue.setOnClickListener {
            // Since Google Sign-In is removed, this should navigate to the dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvSwitchAccount.setOnClickListener {
            finish()
        }
    }
}
