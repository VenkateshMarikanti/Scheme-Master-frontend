package com.simats.schememasters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.simats.schememasters.adapters.SchemeAdapter
import com.simats.schememasters.models.FarmerSchemesResponse
import com.simats.schememasters.models.StudentScheme
import com.simats.schememasters.models.StudentSchemesResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllSchemesActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvAllSchemes: RecyclerView
    private lateinit var tvCount: TextView
    private lateinit var paginationNumbers: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: SchemeAdapter

    private var allCombinedSchemes: MutableList<StudentScheme> = mutableListOf()
    private var currentPage: Int = 1
    private val itemsPerPage: Int = 10 // Increased items per page for better UX

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_schemes)

        etSearch = findViewById(R.id.etSearchSchemes)
        rvAllSchemes = findViewById(R.id.rvAllSchemes)
        tvCount = findViewById(R.id.tvCount)
        paginationNumbers = findViewById(R.id.paginationNumbers)
        progressBar = findViewById(R.id.progressBar)

        rvAllSchemes.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = SchemeAdapter(emptyList()) { scheme ->
            val intent = when {
                scheme.schemeName.contains("Post Matric", ignoreCase = true) -> Intent(this, PostMatricDetailsActivity::class.java)
                scheme.schemeName.contains("National Means", ignoreCase = true) -> Intent(this, NationalMeansDetailsActivity::class.java)
                scheme.schemeName.contains("Laptop", ignoreCase = true) -> Intent(this, LaptopDetailsActivity::class.java)
                scheme.schemeName.contains("State Scholarship", ignoreCase = true) -> Intent(this, StateScholarshipDetailsActivity::class.java)
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
        rvAllSchemes.adapter = adapter

        setupSearch()
        fetchAllSchemes("")
        setupBottomNavigation()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable { fetchAllSchemes(s.toString()) }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAllSchemes(query: String) {
        progressBar.visibility = View.VISIBLE
        allCombinedSchemes.clear()
        
        // Fetch Student Schemes
        RetrofitClient.instance.getStudentSchemes(query).enqueue(object : Callback<StudentSchemesResponse> {
            override fun onResponse(call: Call<StudentSchemesResponse>, response: Response<StudentSchemesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    allCombinedSchemes.addAll(response.body()!!.data)
                }
                fetchFarmerSchemes(query)
            }
            override fun onFailure(call: Call<StudentSchemesResponse>, t: Throwable) {
                fetchFarmerSchemes(query)
            }
        })
    }

    private fun fetchFarmerSchemes(query: String) {
        RetrofitClient.instance.getFarmerSchemes(query).enqueue(object : Callback<FarmerSchemesResponse> {
            override fun onResponse(call: Call<FarmerSchemesResponse>, response: Response<FarmerSchemesResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val farmerData = response.body()!!.data
                    farmerData.forEach { f ->
                        allCombinedSchemes.add(StudentScheme(f.id, f.schemeName, f.aadharName, f.landType, f.eligibilityCriteria, f.specifications))
                    }
                }
                currentPage = 1
                updateUI()
            }
            override fun onFailure(call: Call<FarmerSchemesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                updateUI()
            }
        })
    }

    private fun updateUI() {
        val totalItems = allCombinedSchemes.size
        tvCount.text = "Found $totalItems schemes in total"

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, totalItems)
        
        if (startIndex < totalItems) {
            val pageItems = allCombinedSchemes.subList(startIndex, endIndex)
            adapter.updateList(pageItems)
        } else {
            adapter.updateList(emptyList())
        }

        setupPaginationButtons(totalItems)
    }

    private fun setupPaginationButtons(totalItems: Int) {
        paginationNumbers.removeAllViews()
        val numPages = Math.ceil(totalItems.toDouble() / itemsPerPage).toInt()

        if (numPages <= 1) return

        for (i in 1..numPages) {
            val tv = TextView(this).apply {
                text = i.toString()
                textSize = 16f
                setPadding(30, 10, 30, 10)
                gravity = Gravity.CENTER
                setTextColor(if (i == currentPage) Color.WHITE else Color.parseColor("#0D9488"))
                setBackgroundResource(if (i == currentPage) R.drawable.bg_badge_student else 0)
                typeface = if (i == currentPage) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                
                setOnClickListener {
                    currentPage = i
                    updateUI()
                    rvAllSchemes.scrollToPosition(0)
                }
            }
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }
            paginationNumbers.addView(tv, params)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_schemes
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_schemes -> true
                R.id.nav_upload -> {
                    startActivity(Intent(this, UploadDocsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
