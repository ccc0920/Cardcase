package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VeriFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VeriFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var countdownTextView: TextView
    private lateinit var resendOTPTextView: TextView
    private lateinit var resentButton: Button
    private var countDownTimer: CountDownTimer? = null
    private val START_TIME_IN_MILLIS: Long = 60000 // 60 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_veri, container, false)
        val back: ImageButton =root.findViewById(R.id.backbutton)
        back.setOnClickListener {
            val navigator = findNavController()
            navigator.navigate(R.id.emailFragment)
        }


        val verify1 = root.findViewById<EditText>(R.id.verify1)
        val verify2 = root.findViewById<EditText>(R.id.verify2)
        val verify3 = root.findViewById<EditText>(R.id.verify3)
        val verify4 = root.findViewById<EditText>(R.id.verify4)
        val verifyButton = root.findViewById<Button>(R.id.verifybutton)
        resentButton = root.findViewById(R.id.resentbutton)
        val editTexts = listOf(verify1, verify2, verify3, verify4)
        countdownTextView = root.findViewById(R.id.countdownTextView)

        // 自动跳到下一个输入框
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
            })
        }


        // 设置验证码按钮点击事件
        verifyButton.setOnClickListener {
            val otp = "${verify1.text}${verify2.text}${verify3.text}${verify4.text}"
            if (otp.length == 4&&otp=="1234") {
                // 在这里实现验证逻辑
                //Toast.makeText(this, "OTP: $otp", Toast.LENGTH_SHORT).show()

                //数据库验证码是否相同逻辑
                Toast.makeText(requireContext(), "Verify Success!", Toast.LENGTH_SHORT).show()
                val navigator = findNavController()
                navigator.navigate(R.id.info3Fragment)
            } else {
                Toast.makeText(requireContext(), "Please enter all digits", Toast.LENGTH_SHORT).show()
            }
        }

        resentButton.setOnClickListener{
            //数据库？
            resentButton.isEnabled = false
            startTimer()
            Toast.makeText(requireContext(), "OTP Resent", Toast.LENGTH_SHORT).show()
        }

        startTimer()
        return root
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VeriFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VeriFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}