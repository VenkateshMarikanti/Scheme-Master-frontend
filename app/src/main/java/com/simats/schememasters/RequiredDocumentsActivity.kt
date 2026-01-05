package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RequiredDocumentsActivity : AppCompatActivity() {
    
    private lateinit var tvSchemeName: TextView
    private lateinit var layoutCheckboxes: LinearLayout
    private val checkBoxes = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_required_documents)

        val schemeId = intent.getIntExtra("SCHEME_ID", 1)
        val schemeName = intent.getStringExtra("SCHEME_NAME") ?: "Scheme"
        
        tvSchemeName = findViewById(R.id.tvSchemeNameTitle)
        layoutCheckboxes = findViewById(R.id.layoutCheckboxes)
        
        tvSchemeName.text = schemeName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupCheckboxes(schemeName)

        val btnValidate = findViewById<Button>(R.id.btnValidate)
        btnValidate.setOnClickListener {
            val allChecked = checkBoxes.all { it.isChecked }
            if (allChecked) {
                // Pass the list of documents to the upload activity
                val docList = checkBoxes.map { it.text.toString() }.toTypedArray()
                val intent = Intent(this, UploadDocsActivity::class.java)
                intent.putExtra("SCHEME_ID", schemeId)
                intent.putExtra("SCHEME_NAME", schemeName)
                intent.putExtra("REQUIRED_DOCS", docList)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please check all boxes to proceed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCheckboxes(schemeName: String) {
        layoutCheckboxes.removeAllViews()
        checkBoxes.clear()

        val docs = when {
            schemeName.contains("Farmer", ignoreCase = true) || 
            schemeName.contains("Kisan", ignoreCase = true) ||
            schemeName.contains("Soil", ignoreCase = true) -> {
                listOf("Aadhaar Card", "Land Records (Pattadar Passbook)", "Bank Passbook", "Identity Proof", "Address Proof")
            }
            schemeName.contains("Post Matric", ignoreCase = true) || 
            schemeName.contains("Scholarship", ignoreCase = true) -> {
                listOf("Aadhaar Card", "Caste Certificate", "Income Certificate", "Previous Year Marks Memo", "Bank Passbook")
            }
            schemeName.contains("Laptop", ignoreCase = true) -> {
                listOf("Aadhaar Card", "Student ID", "Caste Certificate", "Income Certificate", "Bonafide Certificate")
            }
            else -> {
                listOf("Aadhaar Card", "Identity Proof", "Address Proof", "Caste Certificate", "Income Certificate")
            }
        }

        docs.forEach { docName ->
            val checkBox = CheckBox(this).apply {
                text = docName
                textSize = 16f
                setPadding(20, 30, 0, 30)
                setTextColor(resources.getColor(R.color.black, null))
            }
            layoutCheckboxes.addView(checkBox)
            checkBoxes.add(checkBox)
        }
    }
}
