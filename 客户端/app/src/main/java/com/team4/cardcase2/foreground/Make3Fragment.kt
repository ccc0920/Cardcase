package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R

class Make3Fragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_make3, container, false)

        val args = arguments ?: Bundle()
        val cardId = args.getInt("cardId", -1)
        val cardName = args.getString("cardName", "Unknown")!!
        val material = args.getString("material", "Matte Paper")!!
        val qty = args.getInt("qty", 50)
        val recipName = args.getString("recipName", "")!!
        val recipPhone = args.getString("recipPhone", "")!!
        val recipAddress = args.getString("recipAddress", "")!!

        val unitPrice = unitPriceFor(material)
        val subtotal = qty * unitPrice
        val shipping = 12.0
        val total = subtotal + shipping

        root.findViewById<TextView>(R.id.reviewCardName).text = cardName
        root.findViewById<TextView>(R.id.reviewMaterial).text = material
        root.findViewById<TextView>(R.id.reviewQty).text = qty.toString()
        root.findViewById<TextView>(R.id.reviewUnitPrice).text = "¥%.2f".format(unitPrice)
        root.findViewById<TextView>(R.id.reviewSubtotal).text = "¥%.2f".format(subtotal)
        root.findViewById<TextView>(R.id.reviewShipping).text = "¥%.2f".format(shipping)
        root.findViewById<TextView>(R.id.reviewTotal).text = "¥%.2f".format(total)
        root.findViewById<TextView>(R.id.reviewName).text = recipName
        root.findViewById<TextView>(R.id.reviewPhone).text = recipPhone
        root.findViewById<TextView>(R.id.reviewAddress).text = recipAddress

        root.findViewById<TextView>(R.id.backMake3).setOnClickListener {
            findNavController().popBackStack()
        }

        root.findViewById<Button>(R.id.placeOrderButton).setOnClickListener {
            val orderId = saveOrder(cardId, cardName, material, qty, unitPrice, total, recipName, recipPhone, recipAddress)
            val bundle = Bundle().apply { putLong("orderId", orderId) }
            findNavController().navigate(R.id.action_make3Fragment_to_make4Fragment, bundle)
        }

        return root
    }

    private fun unitPriceFor(material: String): Double = when (material) {
        "Glossy Paper" -> 0.25
        "Plastic"      -> 1.00
        "Wood"         -> 3.00
        else           -> 0.15  // Matte Paper
    }

    private fun saveOrder(
        cardId: Int, cardName: String, material: String, qty: Int,
        unitPrice: Double, total: Double,
        recipName: String, recipPhone: String, recipAddress: String
    ): Long {
        val ctx = requireContext()
        val uid = AppSession.getUserId(ctx)
        val db = ctx.openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uid INTEGER, card_id INTEGER, card_name TEXT,
                material TEXT, qty INTEGER, unit_price REAL, total REAL,
                recip_name TEXT, recip_phone TEXT, recip_address TEXT,
                status TEXT DEFAULT 'pending',
                timestamp INTEGER
            )
        """.trimIndent())
        val cv = ContentValues().apply {
            put("uid", uid)
            put("card_id", cardId)
            put("card_name", cardName)
            put("material", material)
            put("qty", qty)
            put("unit_price", unitPrice)
            put("total", total)
            put("recip_name", recipName)
            put("recip_phone", recipPhone)
            put("recip_address", recipAddress)
            put("status", "pending")
            put("timestamp", System.currentTimeMillis())
        }
        val rowId = db.insert("orders", null, cv)
        db.close()
        return rowId
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = Make3Fragment()
    }
}
