package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team4.cardcase2.entity.LoginInfo
import com.team4.cardcase2.entity.LoginInfo.Companion.toJson
import com.team4.cardcase2.entity.Login_back
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
            } else {
                val info = LoginInfo(email, password)
                val jsonInfo = info.toJson()
                val url = "http://10.0.2.2:8080/api/login"
                val httpRequest = HttpRequest()
                httpRequest.sendInfoRequest(url, jsonInfo) { response, exception ->
                    runOnUiThread {
                        if (exception != null) {
                            Toast.makeText(this, "Request failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        } else {
                            val loginBack = Login_back.fromJson(response.toString())
                            when {
                                loginBack != null && !loginBack.success -> {
                                    Toast.makeText(this, loginBack.message, Toast.LENGTH_SHORT).show()
                                }
                                loginBack != null && loginBack.success -> {
                                    AppSession.saveLoginInfo(this, loginBack.userId, loginBack.token, email)
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        forgotButton.setOnClickListener {
            startActivity(Intent(this, forgot_password::class.java))
        }

        signupLinkButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
