package com.example.finalassignment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

class Map : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var mapTools : GoogleMap
    private lateinit var locationClient : FusedLocationProviderClient
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        answer ->
        if (answer) {
            showLocation()
        } else showToast("Camera permission denied")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_map)

        findViewById<Button>(R.id.btn_show_location).setOnClickListener { showLocation() }

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            val intentVideoRecording = Intent(this, VideoRecording::class.java)
            startActivity(intentVideoRecording)
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showLocation() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapTools = googleMap

        if (
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient = LocationServices.getFusedLocationProviderClient(this)

            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    val ScarboroughTC = LatLng(43.776035, -79.257713)

                    mapTools.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                    mapTools.addMarker(MarkerOptions().position(ScarboroughTC).title("Scarborough Town Centre"))

                    mapTools.addPolyline(
                        PolylineOptions()
                            .add(userLocation, ScarboroughTC)
                            .color(Color.RED)
                            .width(5f)
                    )

                    val bounds = LatLngBounds.builder()
                        .include(userLocation)
                        .include(ScarboroughTC)
                        .build()

                    val cameraPosition = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                    mapTools.animateCamera(cameraPosition)
                } else showToast("The current location is not available")
            }.addOnFailureListener {
                showToast("The current location is not available")
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}