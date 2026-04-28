package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.R

class Make2Fragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_make2, container, false)

        val cardId = arguments?.getInt("cardId", -1) ?: -1
        val cardName = arguments?.getString("cardName", "") ?: ""

        val spinnerMaterial: Spinner = root.findViewById(R.id.spinnerMaterial)
        val inputQty: EditText = root.findViewById(R.id.inputQty)
        val inputName: EditText = root.findViewById(R.id.inputRecipName)
        val inputPhone: EditText = root.findViewById(R.id.inputRecipPhone)
        val inputAddress: EditText = root.findViewById(R.id.inputRecipAddress)

        root.findViewById<TextView>(R.id.backMake2).setOnClickListener {
            findNavController().popBackStack()
        }

        root.findViewById<TextView>(R.id.nextMake2).setOnClickListener {
            val material = spinnerMaterial.selectedItem?.toString() ?: ""
            val qtyStr = inputQty.text.toString().trim()
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val address = inputAddress.text.toString().trim()

            val qty = qtyStr.toIntOrNull() ?: 0
            when {
                qty < 50 -> { Toast.makeText(requireContext(), "Minimum quantity is 50", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                name.isEmpty() -> { Toast.makeText(requireContext(), "Please enter recipient name", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                phone.isEmpty() -> { Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                address.isEmpty() -> { Toast.makeText(requireContext(), "Please enter delivery address", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            }

            val bundle = Bundle().apply {
                putInt("cardId", cardId)
                putString("cardName", cardName)
                putString("material", material)
                putInt("qty", qty)
                putString("recipName", name)
                putString("recipPhone", phone)
                putString("recipAddress", address)
            }
            findNavController().navigate(R.id.make3Fragment, bundle)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = Make2Fragment()
    }
}
