package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class forgot_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        val emailEditText = findViewById<EditText>(R.id.email_string)
        val loadQuestionButton = findViewById<Button>(R.id.loadQuestionButton)
        val questionSection = findViewById<LinearLayout>(R.id.questionSection)
        val questionText = findViewById<TextView>(R.id.questionText)
        val answerEditText = findViewById<EditText>(R.id.answerEditText)
        val nextButton = findViewById<Button>(R.id.NextButton)
        val backButton = findViewById<ImageButton>(R.id.backbutton)

        loadQuestionButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val question = AppSession.getSecurityQuestion(this, email)
            if (question.isEmpty()) {
                Toast.makeText(this,
                    "No security question found. This only works for accounts registered on this device with the security question feature.",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            questionText.text = question
            questionSection.visibility = View.VISIBLE
        }

        nextButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val answer = answerEditText.text.toString().trim()
            if (answer.isEmpty()) {
                Toast.makeText(this, "Please enter your answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (AppSession.checkSecurityAnswer(this, email, answer)) {
                // Identity verified — attempt server-side password reset
                val intent = Intent(this, reset_password::class.java).apply {
                    putExtra("email", email)
                    putExtra("verification", "")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Incorrect answer. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener { finish() }
    }
}
