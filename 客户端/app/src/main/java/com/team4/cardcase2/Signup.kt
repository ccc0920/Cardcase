package com.team4.cardcase2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.content.Intent
import android.widget.ImageButton
import com.team4.cardcase2.entity.Info

class Signup : AppCompatActivity() {

    private val securityQuestions = listOf(
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What city were you born in?",
        "What was the name of your elementary school?",
        "What is the name of the street you grew up on?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val fullNameEditText = findViewById<EditText>(R.id.fullname_string)
        val emailEditText = findViewById<EditText>(R.id.email_string)
        val passwordEditText = findViewById<EditText>(R.id.password_string)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmpassword_string)
        val spinnerQ = findViewById<Spinner>(R.id.spinnerSecurityQuestion)
        val answerEditText = findViewById<EditText>(R.id.securityAnswer)
        val signupButton = findViewById<Button>(R.id.signUpButton)
        val logInLinkButton = findViewById<Button>(R.id.logInLink_button)
        val backButton = findViewById<ImageButton>(R.id.backbutton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, securityQuestions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerQ.adapter = adapter

        signupButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val question = spinnerQ.selectedItem?.toString() ?: ""
            val answer = answerEditText.text.toString().trim()

            when {
                fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ->
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                password.length < 6 ->
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                password != confirmPassword ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                answer.isEmpty() ->
                    Toast.makeText(this, "Please provide a security answer", Toast.LENGTH_SHORT).show()
                else -> {
                    if (AppSession.isEmailRegistered(this, email)) {
                        Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Generate a stable local userId from email hash
                    val localUserId = (email.hashCode() and 0x7FFFFFFF).coerceAtLeast(1)
                    val localToken = "local_${localUserId}"

                    AppSession.saveLocalPassword(this, email, password)
                    AppSession.saveSecurityQA(this, email, question, answer)
                    AppSession.saveLoginInfo(this, localUserId, localToken, email)
                    AppSession.setUserName(this, fullName)

                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
            }
        }

        logInLinkButton.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
        backButton.setOnClickListener { finish() }
    }
}
