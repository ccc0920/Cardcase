package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team4.cardcase2.foreground.MainActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val emailEditText = findViewById<EditText>(R.id.email_string)
        val passwordEditText = findViewById<EditText>(R.id.login_password_string)
        val loginButton = findViewById<Button>(R.id.logInButton)
        val forgotButton = findViewById<Button>(R.id.forgetpassword_button)
        val signupLinkButton = findViewById<Button>(R.id.signUpLink_button)
        val backButton = findViewById<ImageButton>(R.id.backbutton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (AppSession.checkLocalPassword(this, email, password)) {
                // Ensure userId and token are set (generate if missing)
                var userId = AppSession.getUserId(this)
                if (userId == 0) {
                    userId = (email.hashCode() and 0x7FFFFFFF).coerceAtLeast(1)
                }
                val token = AppSession.getToken(this).ifEmpty { "local_$userId" }
                AppSession.saveLoginInfo(this, userId, token, email)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
            }
        }

        forgotButton.setOnClickListener {
            startActivity(Intent(this, forgot_password::class.java))
        }
        signupLinkButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
        backButton.setOnClickListener { finish() }
    }
}
