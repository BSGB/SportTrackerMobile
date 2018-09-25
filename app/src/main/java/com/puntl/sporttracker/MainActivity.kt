package com.puntl.sporttracker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.parse.ParseAnalytics
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    enum class SignStatus(val status: Boolean) {
        SIGN_IN(true),
        SIGN_UP(false)
    }

    private var signStatus = SignStatus.SIGN_IN.status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ParseAnalytics.trackAppOpenedInBackground(intent)

        //hide action bar
        supportActionBar?.hide()

        //set sign mode
        switchSignMode()

        //sign method switcher listener
        signTextView.setOnClickListener {
            signStatus = !signStatus
            switchSignMode()
        }

        //virtual keyboard enter listener
        passwordEditText.setOnKeyListener {
            view, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) onSignClick(view)
            false
        }
    }

    fun onSignClick(view: View) {
        Log.i("Sign", "Sign button clicked")
        if(isInputCorrect()) {
            Log.i("Sign", "User input is correct")
            when(signStatus) {
                SignStatus.SIGN_IN.status -> {
                    Log.i("Sign", "Starting sign in process")
                    val username = usernameEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    ParseUser.logInInBackground(username, password) {
                        user, exception ->
                        if(user != null) {
                            Log.i("Sign", "User signed in successfully, switching intent")
                            val homeIntent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(homeIntent)
                        } else {
                            Log.e("Sign", "Error while signing in user")
                            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                SignStatus.SIGN_UP.status -> {
                    Log.i("Sign", "Starting sign up process")
                    val email = emailEditText.text.toString()
                    val username = usernameEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    val parseUser = ParseUser()
                    parseUser.username = username
                    parseUser.email = email
                    parseUser.setPassword(password)

                    parseUser.signUpInBackground {
                        exception ->
                        if(exception == null) {
                            Log.i("Sign", "User signed up successfully")
                            Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()
                            signStatus = !signStatus
                            switchSignMode()
                        } else {
                            Log.e("Sign", "Error while creating user")
                            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Log.e("Login", "User input incorrect")
            Toast.makeText(this, "All fields have to be filled.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInputCorrect(): Boolean {
        return when (signStatus) {
            SignStatus.SIGN_IN.status -> {
                usernameEditText.text.isNotEmpty() && usernameEditText.text.isNotBlank()
                && passwordEditText.text.isNotEmpty() && passwordEditText.text.isNotBlank()
            }
            SignStatus.SIGN_UP.status -> {
                usernameEditText.text.isNotEmpty() && usernameEditText.text.isNotBlank()
                && passwordEditText.text.isNotEmpty() && passwordEditText.text.isNotBlank()
                && emailEditText.text.isNotEmpty() && emailEditText.text.isNotBlank()
            } else -> false
        }
    }

    private fun switchSignMode() {
        emailEditText.text.clear()
        usernameEditText.text.clear()
        passwordEditText.text.clear()

        when (signStatus) {
            SignStatus.SIGN_IN.status -> {
                emailEditText.visibility = View.INVISIBLE
                signButton.text = getString(R.string.sign_in)
                signTextView.text = getString(R.string.sign, getString(R.string.sign_up))
            }
            SignStatus.SIGN_UP.status -> {
                emailEditText.visibility = View.VISIBLE
                signButton.text = getString(R.string.sign_up)
                signTextView.text = getString(R.string.sign, getString(R.string.sign_in))
            }
        }
    }
}
