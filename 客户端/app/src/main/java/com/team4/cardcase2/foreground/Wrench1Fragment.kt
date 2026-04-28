package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Wrench1Fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    data class LocalOrder(
        val id: Long,
        val cardName: String,
        val material: String,
        val qty: Int,
        val total: Double,
        val status: String,
        val recipName: String,
        val recipAddress: String
    )

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
        val root = inflater.inflate(R.layout.fragment_wrench1, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.orderRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val orders = loadLocalOrders()
        recyclerView.adapter = LocalOrderAdapter(orders) { order -> showOrderDetail(order) }

        root.findViewById<TextView>(R.id.textView17).text = "My Orders"
        root.findViewById<TextView>(R.id.textView18).text =
            if (orders.isEmpty()) "No orders yet" else "${orders.size} order(s)"

        return root
    }

    private fun loadLocalOrders(): List<LocalOrder> {
        val ctx = requireContext()
        val uid = AppSession.getUserId(ctx)
        val db = ctx.openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uid INTEGER, card_id INTEGER, card_name TEXT,
                material TEXT, qty INTEGER, unit_price REAL, total REAL,
                recip_name TEXT, recip_phone TEXT, recip_address TEXT,
                status TEXT DEFAULT 'pending', timestamp INTEGER
            )
        """.trimIndent())
        val cursor = db.rawQuery(
            "SELECT * FROM orders WHERE uid = ? ORDER BY timestamp DESC",
            arrayOf(uid.toString())
        )
        val list = mutableListOf<LocalOrder>()
        if (cursor.moveToFirst()) {
            do {
                list.add(LocalOrder(
                    id          = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cardName    = cursor.getString(cursor.getColumnIndexOrThrow("card_name")) ?: "",
                    material    = cursor.getString(cursor.getColumnIndexOrThrow("material")) ?: "",
                    qty         = cursor.getInt(cursor.getColumnIndexOrThrow("qty")),
                    total       = cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                    status      = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "pending",
                    recipName   = cursor.getString(cursor.getColumnIndexOrThrow("recip_name")) ?: "",
                    recipAddress= cursor.getString(cursor.getColumnIndexOrThrow("recip_address")) ?: ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    private fun showOrderDetail(order: LocalOrder) {
        val ctx = context ?: return
        val statusLabel = if (order.status == "delivered") "Delivered" else "In Delivery"
        AlertDialog.Builder(ctx)
            .setTitle("Order #${order.id}")
            .setMessage(
                "Card: ${order.cardName}\n" +
                "Material: ${order.material}\n" +
                "Quantity: ${order.qty}\n" +
                "Total: ¥%.2f\n".format(order.total) +
                "Deliver to: ${order.recipName}\n${order.recipAddress}\n" +
                "Status: $statusLabel"
            )
            .setPositiveButton("Close", null)
            .show()
    }

    private inner class LocalOrderAdapter(
        private val orders: List<LocalOrder>,
        private val onClick: (LocalOrder) -> Unit
    ) : RecyclerView.Adapter<LocalOrderAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(android.R.id.text1)
            val sub: TextView   = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val o = orders[position]
            val statusLabel = if (o.status == "delivered") "Delivered" else "In Delivery"
            holder.title.text = "#${o.id}  ${o.cardName}  ¥%.2f".format(o.total)
            holder.sub.text   = "${o.material} × ${o.qty}  [$statusLabel]  → ${o.recipName}"
            holder.itemView.setOnClickListener { onClick(o) }
        }

        override fun getItemCount() = orders.size
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Wrench1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
