package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class reset_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)

        val newPasswordEdit = findViewById<EditText>(R.id.newpassword_string)
        val confirmPassEdit = findViewById<EditText>(R.id.confirm_password_string)
        val resetButton = findViewById<Button>(R.id.ResetButton)
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        val email = intent.getStringExtra("email") ?: ""

        resetButton.setOnClickListener {
            val newPass = newPasswordEdit.text.toString().trim()
            val confirmPass = confirmPassEdit.text.toString().trim()

            when {
                newPass.isEmpty() || confirmPass.isEmpty() ->
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                newPass.length < 6 ->
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                newPass != confirmPass ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    AppSession.saveLocalPassword(this, email, newPass)
                    Toast.makeText(this, "Password updated! Please log in.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }
        }

        backButton.setOnClickListener { finish() }
    }
}
