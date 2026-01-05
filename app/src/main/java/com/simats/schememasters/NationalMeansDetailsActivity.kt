package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NationalMeansDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_national_means_details)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnCheckDocs).setOnClickListener {
            // For now, navigating to the same documents flow
            // You can make this dynamic later to show different documents
            val intent = Intent(this, RequiredDocumentsActivity::class.java)
            intent.putExtra("SCHEME_NAME", "National Means-cum-Merit Scholarship")
            startActivity(intent)
        }
    }
}