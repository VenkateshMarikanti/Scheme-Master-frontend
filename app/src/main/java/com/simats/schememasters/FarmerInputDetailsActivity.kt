package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FarmerInputDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_input_details)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnCheckDocs).setOnClickListener {
            val intent = Intent(this, RequiredDocumentsActivity::class.java)
            intent.putExtra("SCHEME_NAME", "State Farmer Input Subsidy")
            startActivity(intent)
        }
    }
}