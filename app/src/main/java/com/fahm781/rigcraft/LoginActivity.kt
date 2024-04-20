package com.fahm781.rigcraft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView
    private lateinit var forgotPassword: TextView


    //this method checks if the user is already logged in, also this function is causing the crash when logged in
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //send the user to MainActivity for now
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.buttonLogin)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.regNow)

        textView.setOnClickListener {
            val intent = Intent(applicationContext, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonLogin.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isEmpty()) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Enter an email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Enter a Password into the field(s)", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = ProgressBar.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Logged In Successfully.",Toast.LENGTH_SHORT).show()
                        auth.currentUser
                        //Send the user to the next relevant page
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Email Or Password Incorrect.", Toast.LENGTH_SHORT).show()

                    }
                }

        }
        var lastPasswordResetTime: Long = 0
        forgotPassword = findViewById(R.id.forgotPassword)
        forgotPassword.setOnClickListener {
            val email = editTextEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your registered email", Toast.LENGTH_SHORT).show()
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastPasswordResetTime < 5 * 60 * 1000) {
                    // If less than 5 minutes have passed since the last request, show a message
                    Toast.makeText(this, "Please wait for 5 minutes before requesting another password reset", Toast.LENGTH_SHORT).show()
                } else {
                    // If 5 minutes or more have passed, proceed with the password reset request
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                                // Update the time of the last password reset request
                                lastPasswordResetTime = currentTime
                            } else {
                                Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}