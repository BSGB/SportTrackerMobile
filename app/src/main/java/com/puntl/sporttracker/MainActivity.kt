package com.puntl.sporttracker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    enum class SignStatus {
        LOGIN,
        REGISTER
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private var signStatus = SignStatus.LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hide action bar
        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            val homeIntent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(homeIntent)
        }

        switchSignEditText.setOnClickListener {
            signStatus = when (signStatus) {
                SignStatus.LOGIN -> {
                    SignStatus.REGISTER
                }

                SignStatus.REGISTER -> {
                    SignStatus.LOGIN
                }
            }
            updateUI()
        }

        //virtual keyboard enter listener
        passwordEditText.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) onSignClick(view)
            false
        }
    }

    //do not let user go back to f.ex HomeActivity without signing in
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun onSignClick(view: View) {
        val userEmail = emailEditText.text.toString().trim()
        val userPassword = passwordEditText.text.toString().trim()

        if (isInputCorrect(userEmail, userPassword)) {
            when (signStatus) {
                SignStatus.LOGIN -> loginUser(userEmail, userPassword)
                SignStatus.REGISTER -> registerUser(userEmail, userPassword)
            }
        }
    }

    private fun isInputCorrect(userEmail: String, userPassword: String): Boolean {
        return if (userEmail.isNullOrBlank() || userEmail.isNullOrEmpty()
                || userPassword.isNullOrBlank() || userPassword.isNullOrEmpty()) {
            Toast.makeText(this, "Both email and password have to be filled.", Toast.LENGTH_LONG).show()
            false
        } else true
    }

    private fun registerUser(userEmail: String, userPassword: String) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "User registered successfully.", Toast.LENGTH_LONG).show()
                        signStatus = SignStatus.LOGIN
                        updateUI()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun loginUser(userEmail: String, userPassword: String) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val homeIntent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(homeIntent)
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun updateUI() {
        switchSignEditText.text = when (signStatus) {
            SignStatus.LOGIN -> {
                signButton.text = getString(R.string.login)
                getString(R.string.switch_to_register)
            }

            SignStatus.REGISTER -> {
                signButton.text = getString(R.string.register)
                getString(R.string.switch_to_login)
            }
        }
    }
}
