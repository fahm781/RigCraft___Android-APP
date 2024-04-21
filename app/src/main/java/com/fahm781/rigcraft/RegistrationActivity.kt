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


class RegistrationActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var editTextPassword2: TextInputEditText
    private lateinit var buttonRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var textView: TextView

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        editTextPassword2 = findViewById(R.id.confirmPassword)
        buttonRegister = findViewById(R.id.buttonReg)
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.lgnNow)

        textView.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonRegister.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val password2 = editTextPassword2.text.toString()

            if (email.isEmpty()) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Enter an email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password2.isEmpty()) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Enter a Password into the field(s)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if password is at least 8 characters long
            if (password.length < 8) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Password should be at least 8 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //check if password matches the confirm password
            if (password != password2) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = ProgressBar.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            baseContext,
                            "Account Created.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val user = auth.currentUser
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(baseContext,"Authentication failed.",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
