package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Wrench2Fragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var bioInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_wrench2, container, false)

        // Get references to EditTexts from the existing layout
        // Note: Using the existing IDs from the layout
        nameInput = root.findViewById(R.id.editTextTextPersonName3)
        phoneInput = root.findViewById(R.id.editTextTextPersonName4)
        bioInput = root.findViewById(R.id.editTextTextPersonName5)

        val saveButton: Button = root.findViewById(R.id.complete)

        // Load current user info
        val ctx = requireContext()
        val email = AppSession.getEmail(ctx)
        if (email.isNotEmpty()) {
            emailInput = EditText(ctx)
            emailInput.text = android.text.SpannableStringBuilder(email)
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()
            val bio = bioInput.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(ctx, "请输入姓名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user info (local storage for now)
            AppSession.setUserName(ctx, name)
            AppSession.setUserPhone(ctx, phone)
            AppSession.setUserBio(ctx, bio)

            Toast.makeText(ctx, "保存成功", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Wrench2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}