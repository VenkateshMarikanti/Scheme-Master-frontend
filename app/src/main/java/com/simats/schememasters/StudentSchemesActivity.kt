package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.schememasters.adapters.SchemeAdapter
import com.simats.schememasters.models.StudentSchemesResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentSchemesActivity : AppCompatActivity() {

    private lateinit var rvSchemes: RecyclerView
    private lateinit var tvCount: TextView
    private lateinit var adapter: SchemeAdapter
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_schemes)

        rvSchemes = findViewById(R.id.rvStudentSchemes)
        tvCount = findViewById(R.id.tvCount)
        etSearch = findViewById(R.id.etSearchStudentSchemes)
        rvSchemes.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        adapter = SchemeAdapter(emptyList()) { scheme ->
            val intent = when {
                scheme.schemeName.contains("Post Matric", ignoreCase = true) -> Intent(this, PostMatricDetailsActivity::class.java)
                scheme.schemeName.contains("National Means", ignoreCase = true) -> Intent(this, NationalMeansDetailsActivity::class.java)
                scheme.schemeName.contains("Laptop", ignoreCase = true) -> Intent(this, LaptopDetailsActivity::class.java)
                scheme.schemeName.contains("State Scholarship", ignoreCase = true) -> Intent(this, StateScholarshipDetailsActivity::class.java)
                else -> Intent(this, RequiredDocumentsActivity::class.java).apply {
                    putExtra("SCHEME_ID", scheme.id)
                    putExtra("SCHEME_NAME", scheme.schemeName)
                }
            }
            startActivity(intent)
        }
        rvSchemes.adapter = adapter

        setupSearch()
        fetchStudentSchemes("")
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fetchStudentSchemes(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchStudentSchemes(search: String) {
        RetrofitClient.instance.getStudentSchemes(search).enqueue(object : Callback<StudentSchemesResponse> {
            override fun onResponse(call: Call<StudentSchemesResponse>, response: Response<StudentSchemesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val schemesResponse = response.body()!!
                    if (schemesResponse.status == "success") {
                        val schemes = schemesResponse.data
                        adapter.updateList(schemes)
                        tvCount.text = "Found ${schemes.size} schemes for students"
                    }
                }
            }

            override fun onFailure(call: Call<StudentSchemesResponse>, t: Throwable) {
                Toast.makeText(this@StudentSchemesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
