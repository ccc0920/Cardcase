package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class forgot_password_verify : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Email verification removed — go directly to Login
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}
