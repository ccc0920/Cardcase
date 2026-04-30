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
        val args = arguments ?: Bundle()

        val orderId = args.getLong("orderId", -1L)
        if (orderId > 0) {
            root.findViewById<TextView>(R.id.orderIdLabel).text = "Order #$orderId"
        }

        val cardName = args.getString("cardName", "")
        val material = args.getString("material", "")
        val qty = args.getInt("qty", 0)
        val total = args.getDouble("total", 0.0)
        val recipName = args.getString("recipName", "")
        val recipPhone = args.getString("recipPhone", "")
        val recipAddress = args.getString("recipAddress", "")

        val summary = buildString {
            appendLine("Card: $cardName")
            appendLine("Material: $material")
            appendLine("Quantity: $qty")
            appendLine("Total: ¥%.2f".format(total))
            appendLine("─────────────────")
            appendLine("Recipient: $recipName")
            appendLine("Phone: $recipPhone")
            append("Address: $recipAddress")
        }
        root.findViewById<TextView>(R.id.orderSummaryText).text = summary

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
