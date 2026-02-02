package com.simats.schememasters

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
import com.simats.schememasters.adapters.FarmerSchemeAdapter
import com.simats.schememasters.models.FarmerScheme
import com.simats.schememasters.models.FarmerSchemesResponse
import com.simats.schememasters.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FarmerSchemesActivity : AppCompatActivity() {

    private lateinit var rvSchemes: RecyclerView
    private lateinit var tvCount: TextView
    private lateinit var adapter: FarmerSchemeAdapter
    private lateinit var etSearch: EditText
    private lateinit var paginationNumbers: LinearLayout
    private lateinit var progressBar: ProgressBar

    private var allSchemes: List<FarmerScheme> = listOf()
    private var currentPage: Int = 1
    private val itemsPerPage: Int = 10

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_schemes)

        rvSchemes = findViewById(R.id.rvFarmerSchemes)
        tvCount = findViewById(R.id.tvCount)
        etSearch = findViewById(R.id.etSearchFarmerSchemes)
        paginationNumbers = findViewById(R.id.paginationNumbers)
        progressBar = findViewById(R.id.progressBar)
        
        rvSchemes.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = FarmerSchemeAdapter(emptyList()) { scheme ->
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
        rvSchemes.adapter = adapter

        setupSearch()
        fetchFarmerSchemes("")
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable { fetchFarmerSchemes(s.toString()) }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchFarmerSchemes(search: String) {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getFarmerSchemes(search).enqueue(object : Callback<FarmerSchemesResponse> {
            override fun onResponse(call: Call<FarmerSchemesResponse>, response: Response<FarmerSchemesResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val schemesResponse = response.body()!!
                    if (schemesResponse.status == "success") {
                        allSchemes = schemesResponse.data
                        currentPage = 1
                        updateUI()
                    }
                }
            }

            override fun onFailure(call: Call<FarmerSchemesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@FarmerSchemesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI() {
        val totalItems = allSchemes.size
        tvCount.text = "Found $totalItems schemes for farmers"

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, totalItems)
        
        if (startIndex < totalItems) {
            val pageItems = allSchemes.subList(startIndex, endIndex)
            adapter.updateList(pageItems)
        } else {
            adapter.updateList(emptyList())
        }

        setupPaginationButtons(totalItems)
        rvSchemes.scrollToPosition(0)
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
}
