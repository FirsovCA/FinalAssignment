package com.example.finalassignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalassignment.databinding.ActivityInteractivityBinding

class Interactivity : AppCompatActivity() {
    private lateinit var binding : ActivityInteractivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInteractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVideo.setOnClickListener {
            val intentVideoRecording = Intent(this, VideoRecording::class.java)
            startActivity(intentVideoRecording)
        }

        binding.btnMap.setOnClickListener { goToMapPage() }
        binding.btnNext.setOnClickListener { goToMapPage() }

        binding.btnBack.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun goToMapPage() {
        val intentMap = Intent(this, Map::class.java)
        startActivity(intentMap)
    }
}