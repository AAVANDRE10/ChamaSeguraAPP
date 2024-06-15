package com.example.chamasegura

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.chamasegura.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // Define um local inicial (por exemplo, Lisboa)
        val initialLocation = LatLng(38.736946, -9.142685)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12.0f))

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLocation = latLng
        }

        findViewById<Button>(R.id.buttonConfirmLocation).setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("latitude", selectedLocation.latitude.toFloat())
                putExtra("longitude", selectedLocation.longitude.toFloat())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}