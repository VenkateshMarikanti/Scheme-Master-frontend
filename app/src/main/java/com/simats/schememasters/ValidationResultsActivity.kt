package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.schememasters.adapters.ValidationResultAdapter
import com.simats.schememasters.models.ValidationItem

class ValidationResultsActivity : AppCompatActivity() {

    private lateinit var rvResults: RecyclerView
    private var schemeId: Int = 1
    private var missingDocs: Array<String> = arrayOf()
    private var requiredDocs: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validation_results)

        schemeId = intent.getIntExtra("SCHEME_ID", 1)
        missingDocs = intent.getStringArrayExtra("MISSING_DOCS") ?: arrayOf()
        requiredDocs = intent.getStringArrayExtra("REQUIRED_DOCS") ?: arrayOf()

        rvResults = findViewById(R.id.rvResults)
        rvResults.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnGuidance).setOnClickListener {
            val intent = Intent(this, GuidanceActivity::class.java)
            intent.putExtra("MISSING_DOCS", missingDocs)
            startActivity(intent)
        }

        displayResults()
    }

    private fun displayResults() {
        val validationItems = mutableListOf<ValidationItem>()
        
        // Add required documents with their status
        requiredDocs.forEach { doc ->
            val isMissing = missingDocs.contains(doc)
            validationItems.add(ValidationItem(doc, if (isMissing) "missing" else "uploaded"))
        }

        val adapter = ValidationResultAdapter(validationItems)
        rvResults.adapter = adapter
    }
}
