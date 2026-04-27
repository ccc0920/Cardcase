package com.team4.cardcase2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher
import android.text.Editable
import android.widget.Toast
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.team4.cardcase2.entity.Verify
import com.team4.cardcase2.entity.Verify.Companion.toJson
import com.team4.cardcase2.entity.forgot_verify
import com.team4.cardcase2.entity.forgot_verify.Companion.toJson
import com.team4.cardcase2.entity.signup_back
import com.team4.cardcase2.entity.verify_back


class forgot_password_verify:AppCompatActivity() {

    private lateinit var countdownTextView: TextView
    private lateinit var resendOTPTextView: TextView
    private lateinit var resentButton: Button
    private var countDownTimer: CountDownTimer? = null
    private val START_TIME_IN_MILLIS: Long = 60000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpass_verify) // 确保使用正确的布局文件

//        val verify1 = findViewById<EditText>(R.id.verify1)
//        val verify2 = findViewById<EditText>(R.id.verify2)
//        val verify3 = findViewById<EditText>(R.id.verify3)
//        val verify4 = findViewById<EditText>(R.id.verify4)
        val verifyButton = findViewById<Button>(R.id.verifybutton)
        val backButton = findViewById<ImageButton>(R.id.backbutton)
        resentButton = findViewById(R.id.resent_button)
//        val editTexts = listOf(verify1, verify2, verify3, verify4)
        countdownTextView = findViewById(R.id.countdownTextView)
        val verify = findViewById<EditText>(R.id.verify_box)



        val email_string = intent.getStringExtra("email")!!
        //Log.d("SecondActivity","$email_string 已经被取出")

        // 自动跳到下一个输入框
//        for (i in editTexts.indices) {
//            editTexts[i].addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//                override fun afterTextChanged(s: Editable?) {
//                    if (s?.length == 1 && i < editTexts.size - 1) {
//                        editTexts[i + 1].requestFocus()
//                    }
//                }
//            })
//        }


        // 设置验证码按钮点击事件
        verifyButton.setOnClickListener {
//            val otp = "${verify1.text}${verify2.text}${verify3.text}${verify4.text}"
//            if (otp.length == 4) {
//                // 在这里实现验证逻辑
//                Toast.makeText(this, "OTP: $otp", Toast.LENGTH_SHORT).show()
//
//                //数据库验证码是否相同逻辑
//                val intent = Intent(this, reset_password::class.java)
//                intent.putExtra("email_string","string")
//                startActivity(intent)
//
//            } else {
//                Toast.makeText(this, "Please enter all digits", Toast.LENGTH_SHORT).show()
//            }
            val verificationCode = verify.text.toString().trim()

//            Toast.makeText(this, email_string, Toast.LENGTH_SHORT).show()
            val verify1 = Verify(email_string,verificationCode)

            val jsonInfo = verify1.toJson()
//            Toast.makeText(this, jsonInfo, Toast.LENGTH_SHORT).show()
            val url = "http://10.0.2.2:8080/api/verify-email"
            val httpRequest = HttpRequest()
            httpRequest.sendVerifyRequest(url,jsonInfo){response,exception->
                runOnUiThread {
                    if (exception != null) {
                        // 处理错误
                        println("Request failed: ${exception.message}")
                        Toast.makeText(this, "Request failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        // 处理成功响应
                        val response_string = response.toString()
                        val verifyBackObject = verify_back.fromJson(response_string)
                        when {
                            // 根据具体情况处理响应消息，例如根据返回的特定字段或信息来判断要跳转到哪个界面
                            // 假设根据返回的消息中的某个字段来判断跳转到哪个界面
                            verifyBackObject != null && verifyBackObject.success== false -> {
                                // 创建失败，已存在用户
                                Toast.makeText(this, verifyBackObject.message, Toast.LENGTH_SHORT).show()

                            }
                            verifyBackObject != null && verifyBackObject.success == true -> {
                                // 进一步处理响应，比如跳转到新界面
                                val intent = Intent(this, reset_password::class.java)
                                intent.putExtra("email", email_string)
                                intent.putExtra("verification",verificationCode)
                                startActivity(intent)
                            }
                            else -> {
                                // 默认情况下，做一些通用处理或显示错误消息
                                Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        backButton.setOnClickListener{
            finish()
        }

        resentButton.setOnClickListener {
            //数据库？
            resentButton.isEnabled = false
            startTimer()
            val email = email_string.toString()
            val info = forgot_verify(email)

            val jsonInfo = info.toJson()
//            Toast.makeText(this, jsonInfo, Toast.LENGTH_SHORT).show()
            val url = "http://10.0.2.2:8080/api/resend-verification-code"
            val httpRequest = HttpRequest()
            httpRequest.sendInfoRequest(url, jsonInfo) { response, exception ->
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
                                Toast.makeText(
                                    this,
                                    signupBackObject.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                // 默认情况下，做一些通用处理或显示错误消息
                                Toast.makeText(this, "Unexpected response", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
//                Toast.makeText(this, "OTP Resent", Toast.LENGTH_SHORT).show()
            }
        }

        startTimer()
    }
    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownTextView.text = "Resend OTP in $secondsLeft s"
            }

            override fun onFinish() {
                resentButton.isEnabled = true
            }
        }.start()
    }
}