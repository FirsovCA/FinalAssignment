package com.example.finalassignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalassignment.databinding.ActivityInteractivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Interactivity : AppCompatActivity() {
    private lateinit var binding : ActivityInteractivityBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding = ActivityInteractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createQuestionsSpinner()

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

    private fun createQuestionsSpinner() {
        val usersCollection = firestore.collection("UserInformation")
        val uid = auth.currentUser?.uid

        if(uid != null) {
            usersCollection.document(uid).get()
                .addOnSuccessListener { report ->
                    val savedFirstName = report.data?.get("firstName")
                    val savedFamilyName = report.data?.get("familyName")
                    val savedAge = report.data?.get("age")
                    val savedCountry = report.data?.get("country")
                    val savedCity = report.data?.get("city")

                    val questions = mutableListOf("Select a question")

                    if (savedFirstName != null) {
                        questions.add("What is my name?")
                    }

                    if (savedFamilyName != null) {
                        questions.add("What is my family name?")
                    }

                    if (savedFirstName != null && savedFamilyName != null) {
                        questions.add("What is my full name?")
                    }

                    if (savedAge != null) {
                        questions.add("What year was I born?")
                    }

                    if (savedCountry != null) {
                        questions.add("What country am I living in?")
                    }

                    if (savedCity != null) {
                        questions.add("What city am I living in?")
                    }

                    val questionsAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        questions
                    )

                    questionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerQuestions.adapter = questionsAdapter

                    binding.spinnerQuestions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val question = parent?.getItemAtPosition(position).toString()

                            if (!question.isBlank() && !question.startsWith("Select")) {
                                val text = when (question) {
                                    "What is my name?" -> savedFirstName
                                    "What is my family name?" -> savedFamilyName
                                    "What is my full name?" -> "${savedFirstName} ${savedFamilyName}"
                                    "What year was I born?" -> {
                                        val ageInt = when (savedAge) {
                                            is Long -> savedAge.toInt()
                                            is Int -> savedAge
                                            is String -> savedAge.toIntOrNull()
                                            else -> null
                                        }

                                        if (ageInt != null) {
                                            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                                            (currentYear - ageInt).toString()
                                        } else "Unknown"
                                    }
                                    "What country am I living in?" -> savedCountry
                                    "What city am I living in?" -> savedCity
                                    else -> "Select a question"
                                }

                                binding.textAnswer.text = text as String
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error receiving data", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun goToMapPage() {
        val intentMap = Intent(this, Map::class.java)
        startActivity(intentMap)
    }
}