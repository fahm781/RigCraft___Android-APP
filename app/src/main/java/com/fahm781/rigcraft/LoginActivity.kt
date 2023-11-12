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

    lateinit var editTextEmail: TextInputEditText
    lateinit var editTextPassword: TextInputEditText
    lateinit var buttonLogin: Button
    lateinit var progressBar: ProgressBar;

    //create a Firebase Authentication object
    lateinit var auth: FirebaseAuth
    lateinit var textView: TextView

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
                        Toast.makeText(
                            baseContext,
                            "Logged In Successfully.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val user = auth.currentUser

                        //Send the user to the next relevant page
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Email Or Password Incorrect.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }

        }
    }
}