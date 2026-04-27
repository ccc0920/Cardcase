package com.team4.cardcase2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent;
import android.widget.ImageButton
import com.team4.cardcase2.entity.Info
import com.team4.cardcase2.entity.Info.Companion.fromJson
import com.team4.cardcase2.entity.Info.Companion.toJson
import com.team4.cardcase2.entity.signup_back
import com.team4.cardcase2.entity.signup_back.Companion.fromJson
import okhttp3.Response


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
        val backButton = findViewById<ImageButton>(R.id.backbutton)


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
                val info = Info(fullName,email,password)

                val jsonInfo = info.toJson()
//                Toast.makeText(this, jsonInfo, Toast.LENGTH_SHORT).show()
                val url = "http://10.0.2.2:8080/api/register"
                val httpRequest = HttpRequest()
                httpRequest.sendInfoRequest(url,jsonInfo){response,exception->
                    runOnUiThread {
                        if (exception != null) {
                            // 处理错误
                            println("Request failed: ${exception.message}")
                            Toast.makeText(this, "Request failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        } else {
                            // 处理成功响应
                            val response_string = response.toString()
                            val signupBackObject = signup_back.fromJson(response_string)
                            when {
                                // 根据具体情况处理响应消息，例如根据返回的特定字段或信息来判断要跳转到哪个界面
                                // 假设根据返回的消息中的某个字段来判断跳转到哪个界面
                                signupBackObject != null && signupBackObject.success== false -> {
                                    // 创建失败，已存在用户
                                    Toast.makeText(this, "This email is registered", Toast.LENGTH_SHORT).show()

                                }
                                signupBackObject != null && signupBackObject.success == true -> {
                                    // 跳转到验证界面
                                    val intent = Intent(this, Verify::class.java)
                                    intent.putExtra("email",emailEditText.text.toString().trim())
                                    startActivity(intent)
                                }
                                else -> {
                                    // 默认情况下，做一些通用处理或显示错误消息
                                    Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show()
                                }
                            }
//                            Toast.makeText(this, "Response: $response", Toast.LENGTH_SHORT).show()
//                            // 进一步处理响应，比如跳转到新界面
//                            val intent = Intent(this, Verify::class.java)
//                            intent.putExtra("email",emailEditText.text.toString().trim())
//                            startActivity(intent)
                        }
                    }
                }

            }

        }
        logInLinkbutton.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        backButton.setOnClickListener{
            finish()
        }
    }
}