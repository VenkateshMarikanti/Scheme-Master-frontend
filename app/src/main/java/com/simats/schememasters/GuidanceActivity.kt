package com.simats.schememasters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GuidanceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidance)

        // Initialize Map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnBackToSchemes).setOnClickListener {
            val intent = Intent(this, AllSchemesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnOpenMaps).setOnClickListener {
            // Open Google Maps app with search query for nearby Meeseva centers
            val gmmIntentUri = Uri.parse("geo:0,0?q=Meeseva+center+near+me")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        // Apply Link Listener
        findViewById<TextView>(R.id.tvApplyLink).setOnClickListener {
            val url = "https://www.myscheme.gov.in/" // Official portal link
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set a default location (e.g., center of a city) and add a marker
        val defaultLoc = LatLng(17.3850, 78.4867) // Example: Hyderabad
        mMap.addMarker(MarkerOptions().position(defaultLoc).title("Nearby Center"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 12f))
    }
}
