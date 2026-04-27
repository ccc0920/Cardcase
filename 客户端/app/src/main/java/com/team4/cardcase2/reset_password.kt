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
import com.team4.cardcase2.entity.resetpass
import com.team4.cardcase2.entity.resetpass.Companion.toJson
import com.team4.cardcase2.entity.signup_back

class reset_password:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password) // 确保使用正确的布局文件


        val newpassword_string = findViewById<EditText>(R.id.newpassword_string)
        val confirm_pass_string = findViewById<EditText>(R.id.confirm_password_string)
        val resetButton = findViewById<Button>(R.id.ResetButton)
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        val email_string = intent.getStringExtra("email")!!
        val resetcode = intent.getStringExtra("verification")!!

        resetButton.setOnClickListener {
            val newpass = newpassword_string.text.toString().trim()
            val confirm_pass = confirm_pass_string.text.toString().trim()
//            val email = email_string.toString()

            if (newpass.isEmpty() || confirm_pass.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else if (newpass != confirm_pass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // 处理重设密码逻辑，例如将数据发送到服务器
                // 处理注册逻辑，例如将数据发送到服务器
                val info = resetpass(email_string,resetcode,newpass,confirm_pass)

                val jsonInfo = info.toJson()
//                Toast.makeText(this, jsonInfo, Toast.LENGTH_SHORT).show()
                val url = "http://10.0.2.2:8080/api/reset-password"
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
                                    Toast.makeText(this, signupBackObject.message, Toast.LENGTH_SHORT).show()

                                }
                                signupBackObject != null && signupBackObject.success == true -> {
                                    // 跳转到登录
                                    val intent = Intent(this, Login::class.java)
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
                //Toast.makeText(this, "Full Name: $fullName\nEmail: $email\nPassword: $password", Toast.LENGTH_LONG).show()
            }
        }
        backButton.setOnClickListener{
            finish()
        }
    }
}