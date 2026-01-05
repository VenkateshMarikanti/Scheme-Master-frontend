package com.simats.schememasters

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.schememasters.adapters.FarmerSchemeAdapter
import com.simats.schememasters.adapters.SchemeAdapter
import com.simats.schememasters.models.FarmerSchemesResponse
import com.simats.schememasters.models.StudentSchemesResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllSchemesActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvStudent: RecyclerView
    private lateinit var rvFarmer: RecyclerView
    private lateinit var tvStudentSection: TextView
    private lateinit var tvFarmerSection: TextView
    
    private lateinit var studentAdapter: SchemeAdapter
    private lateinit var farmerAdapter: FarmerSchemeAdapter

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_schemes)

        etSearch = findViewById(R.id.etSearchSchemes)
        rvStudent = findViewById(R.id.rvStudentSchemes)
        rvFarmer = findViewById(R.id.rvFarmerSchemes)
        tvStudentSection = findViewById(R.id.tvStudentSection)
        tvFarmerSection = findViewById(R.id.tvFarmerSection)

        rvStudent.layoutManager = LinearLayoutManager(this)
        rvFarmer.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupAdapters()
        setupSearch()
        fetchAllSchemes("")
    }

    private fun setupAdapters() {
        studentAdapter = SchemeAdapter(emptyList()) { scheme ->
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
        rvStudent.adapter = studentAdapter

        farmerAdapter = FarmerSchemeAdapter(emptyList()) { scheme ->
            val intent = when {
                scheme.schemeName.contains("PM-KISAN", ignoreCase = true) -> Intent(this, PMKisanDetailsActivity::class.java)
                scheme.schemeName.contains("PM Fasal", ignoreCase = true) -> Intent(this, PMFasalDetailsActivity::class.java)
                scheme.schemeName.contains("Soil Health", ignoreCase = true) -> Intent(this, SoilHealthDetailsActivity::class.java)
                else -> Intent(this, RequiredDocumentsActivity::class.java).apply {
                    putExtra("SCHEME_ID", scheme.id)
                    putExtra("SCHEME_NAME", scheme.schemeName)
                }
            }
            startActivity(intent)
        }
        rvFarmer.adapter = farmerAdapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable { fetchAllSchemes(s.toString()) }
                searchHandler.postDelayed(searchRunnable!!, 500) // Debounce delay 500ms
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAllSchemes(query: String) {
        // Fetch Student Schemes
        RetrofitClient.instance.getStudentSchemes(query).enqueue(object : Callback<StudentSchemesResponse> {
            override fun onResponse(call: Call<StudentSchemesResponse>, response: Response<StudentSchemesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    studentAdapter.updateList(data)
                    tvStudentSection.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
                    rvStudent.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
                }
            }
            override fun onFailure(call: Call<StudentSchemesResponse>, t: Throwable) {}
        })

        // Fetch Farmer Schemes
        RetrofitClient.instance.getFarmerSchemes(query).enqueue(object : Callback<FarmerSchemesResponse> {
            override fun onResponse(call: Call<FarmerSchemesResponse>, response: Response<FarmerSchemesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    farmerAdapter.updateList(data)
                    tvFarmerSection.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
                    rvFarmer.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
                }
            }
            override fun onFailure(call: Call<FarmerSchemesResponse>, t: Throwable) {}
        })
    }
}
