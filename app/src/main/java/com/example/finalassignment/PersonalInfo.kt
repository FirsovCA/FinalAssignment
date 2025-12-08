package com.example.finalassignment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalassignment.databinding.ActivityPersonalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonalInfo : AppCompatActivity() {
    private lateinit var binding : ActivityPersonalInfoBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        createAgeSpinner()
        createCountrySpinner()

        binding.btnClearAge.setOnClickListener { binding.spinnerAge.setSelection(0) }
        binding.btnClearCountry.setOnClickListener { binding.spinnerCountry.setSelection(0) }
        binding.btnClearCity.setOnClickListener { binding.spinnerCity.setSelection(0) }

        binding.btnSubmit.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you want to save your data?")
                .setPositiveButton("Yes") { dialog, which ->
                    saveUserInfo()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnNext.setOnClickListener {
            val intentInteractivity = Intent(this, Interactivity::class.java)
            startActivity(intentInteractivity)
        }

        binding.btnBack.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAgeSpinner() {
        val ages = (14..100).map { it.toString() }
        val ageList = mutableListOf("Select age")
        ageList.addAll(ages)

        val ageAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ageList
        )

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAge.adapter = ageAdapter
    }

    private fun createCountrySpinner() {
        val countryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.countries,
            android.R.layout.simple_spinner_item
        )

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = countryAdapter

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val country = parent?.getItemAtPosition(position).toString()
                updateCities(country)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateCities(country: String) {
        val citiesArray = when (country) {
            "United States" -> R.array.usa_cities
            "Canada" -> R.array.canada_cities
            "United Kingdom" -> R.array.uk_cities
            "Germany" -> R.array.germany_cities
            "France" -> R.array.france_cities
            "Australia" -> R.array.australia_cities
            "Japan" -> R.array.japan_cities
            "China" -> R.array.china_cities
            "India" -> R.array.india_cities
            "Brazil" -> R.array.brazil_cities
            else -> R.array.nothing
        }

        val cityAdapter = ArrayAdapter.createFromResource(
            this,
            citiesArray,
            android.R.layout.simple_spinner_item
        )

        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter
    }

    private fun saveUserInfo() {
        val UserInfoCollection = firestore.collection("UserInformation")

        val firstName = binding.etFirstName.text.toString().ifBlank { null }
        val familyName = binding.etFamilyName.text.toString().ifBlank { null }

        val selectedAge = if (binding.spinnerAge.selectedItem.toString().startsWith("Select")) null
        else binding.spinnerAge.selectedItem.toString()

        val selectedCountry = if (binding.spinnerCountry.selectedItem.toString().startsWith("Select")) null
        else binding.spinnerCountry.selectedItem.toString()

        val selectedCity = if (binding.spinnerCity.selectedItem.toString().startsWith("Select")) null
        else binding.spinnerCity.selectedItem.toString()

        val userInfo = hashMapOf(
            "firstName" to firstName,
            "familyName" to familyName,
            "age" to selectedAge,
            "country" to selectedCountry,
            "city" to selectedCity
        )

        val uid = auth.currentUser?.uid
        if(uid != null) {
            UserInfoCollection.document(uid).set(userInfo)
                .addOnSuccessListener { showToast("The information has been successfully saved") }
                .addOnFailureListener { error -> showToast("Error writing information: ${error.message}") }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}