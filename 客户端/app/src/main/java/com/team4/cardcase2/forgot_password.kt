package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team4.cardcase2.entity.Info
import com.team4.cardcase2.entity.Info.Companion.toJson
import com.team4.cardcase2.entity.forgot_verify
import com.team4.cardcase2.entity.forgot_verify.Companion.toJson
import com.team4.cardcase2.entity.signup_back


class forgot_password:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password) // 确保使用正确的布局文件

        val emailEditText = findViewById<EditText>(R.id.email_string)
        val nextButton = findViewById<Button>(R.id.NextButton)
        val backButton = findViewById<ImageButton>(R.id.backbutton)

        nextButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                val info = forgot_verify(email)

                val jsonInfo = info.toJson()
//                Toast.makeText(this, jsonInfo, Toast.LENGTH_SHORT).show()
                val url = "http://10.0.2.2:8080/api/forgot-password"
                val httpRequest = HttpRequest()
                httpRequest.sendInfoRequest(url,jsonInfo) { response, exception ->
                    runOnUiThread {
                        if (exception != null) {
                            // 处理错误
                            println("Request failed: ${exception.message}")
                            Toast.makeText(
                                this,
                                "Request failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // 处理成功响应
                            val response_string = response.toString()
                            val signupBackObject = signup_back.fromJson(response_string)
                            when {
                                // 根据具体情况处理响应消息，例如根据返回的特定字段或信息来判断要跳转到哪个界面
                                // 假设根据返回的消息中的某个字段来判断跳转到哪个界面
                                signupBackObject != null && signupBackObject.success == false -> {
                                    Toast.makeText(
                                        this,
                                        signupBackObject.message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                signupBackObject != null && signupBackObject.success == true -> {
                                    // 跳转到验证界面
                                    val intent = Intent(this, forgot_password_verify::class.java)
                                    intent.putExtra("email", email)
                                    startActivity(intent)
                                }

                                else -> {
                                    // 默认情况下，做一些通用处理或显示错误消息
                                    Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }

        }
        backButton.setOnClickListener{
            finish()
        }
    }
}