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
import com.simats.schememasters.adapters.FarmerSchemeAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_schemes)

        rvSchemes = findViewById(R.id.rvFarmerSchemes)
        tvCount = findViewById(R.id.tvCount)
        etSearch = findViewById(R.id.etSearchFarmerSchemes)
        rvSchemes.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
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
                fetchFarmerSchemes(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchFarmerSchemes(search: String) {
        RetrofitClient.instance.getFarmerSchemes(search).enqueue(object : Callback<FarmerSchemesResponse> {
            override fun onResponse(call: Call<FarmerSchemesResponse>, response: Response<FarmerSchemesResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val schemesResponse = response.body()!!
                    if (schemesResponse.status == "success") {
                        val schemes = schemesResponse.data
                        adapter.updateList(schemes)
                        tvCount.text = "Found ${schemes.size} schemes for farmers"
                    }
                }
            }

            override fun onFailure(call: Call<FarmerSchemesResponse>, t: Throwable) {
                Toast.makeText(this@FarmerSchemesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
