package com.example.finalassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalassignment.databinding.ActivityMainBinding

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Main : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private val google_sign_in_tracking_number = 327557

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignUpGoogle.setOnClickListener {
            val googleIntent = googleSignInClient.signInIntent
            startActivityForResult(googleIntent, google_sign_in_tracking_number)
        }

        binding.btnLoginAnonymous.setOnClickListener { loginAsAnonymous() }

        binding.btnSignUp.setOnClickListener {
            val intentSignUp = Intent(this, SignUp::class.java)
            startActivity(intentSignUp)
        }

        binding.btnLogin.setOnClickListener {
            val intentLogin = Intent(this, Login::class.java)
            startActivity(intentLogin)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == google_sign_in_tracking_number) {
            val huge_report = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val small_required_report = huge_report.result
                val idToken = small_required_report.idToken
                val credentials = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(credentials).addOnCompleteListener { signInTask ->
                    if(signInTask.isSuccessful) {
                        showToast("Login Successful")

                        val intentLogin = Intent(this, PersonalInfo::class.java)
                        startActivity(intentLogin)
                    } else {
                        showToast("Login has failed")
                    }
                }
            } catch (e: ApiException) { showToast("Google sign in has failed : ${e.message}") }
        }
    }

    private fun loginAsAnonymous() {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Login successful")
                val intentPersonalInfo = Intent(this, PersonalInfo::class.java)
                startActivity(intentPersonalInfo)
            } else { showToast("Login has failed") }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}