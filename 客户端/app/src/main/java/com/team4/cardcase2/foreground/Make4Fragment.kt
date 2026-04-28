package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

class Make4Fragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_make4, container, false)

        val orderId = arguments?.getLong("orderId", -1L) ?: -1L
        if (orderId > 0) {
            root.findViewById<TextView>(R.id.orderIdLabel).text = "Order #$orderId"
        }

        root.findViewById<Button>(R.id.continuemaking).setOnClickListener {
            findNavController().navigate(R.id.action_make4Fragment_to_make1Fragment)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = Make4Fragment()
    }
}
