package com.team4.cardcase2.foreground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.team4.cardcase2.R
import android.widget.Toast
import android.content.Intent;


class Signup:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup) // 确保使用正确的布局文件

        // 获取视图引用
        val fullNameEditText = findViewById<EditText>(R.id.fullname_string)
        val emailEditText = findViewById<EditText>(R.id.email_string)
        val passwordEditText = findViewById<EditText>(R.id.password_string)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmpassword_string)
        val signupButton= findViewById<Button>(R.id.signUpButton)
        val logInLinkbutton = findViewById<Button>(R.id.logInLink_button)

        // 设置按钮点击事件监听器
        signupButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // 处理注册逻辑，例如将数据发送到服务器
                Toast.makeText(this, "Full Name: $fullName\nEmail: $email\nPassword: $password", Toast.LENGTH_LONG).show()
            }

        }
        logInLinkbutton.setOnClickListener{
//            val intent = Intent(this, login::class.java)
//            startActivity(intent)
        }
    }
}