package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Start:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start) // 确保使用正确的布局文件

        val signupButton= findViewById<Button>(R.id.signUpButton)
        val loginButton = findViewById<Button>(R.id.LoginButton)

        signupButton.setOnClickListener{
            val intent = Intent(this,Signup::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener{
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

    }
}