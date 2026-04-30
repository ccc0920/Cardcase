package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.Start

class SettingsFragment : Fragment() {

    private var _root: View? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        _root = root

        populateHeader(root)

        // Edit profile button
        val editProfileButton: ImageButton = root.findViewById(R.id.imageView9)
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_infoFragment2)
        }

        // Orders — show local order history
        root.findViewById<LinearLayout>(R.id.orderAllRow).setOnClickListener {
            showOrderDialog(null)
        }
        root.findViewById<LinearLayout>(R.id.orderDeliveryRow).setOnClickListener {
            showOrderDialog("pending")
        }
        root.findViewById<LinearLayout>(R.id.orderAftersaleRow).setOnClickListener {
            showOrderDialog("delivered")
        }

        // Contact Us
        val contactRow: LinearLayout = root.findViewById(R.id.group2)
        contactRow.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ccc0920/Cardcase"))
            startActivity(intent)
        }

        // Privacy & Security
        val privacyRow: LinearLayout = root.findViewById(R.id.group3)
        privacyRow.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cardcase.app/privacy"))
            startActivity(intent)
        }

        // About CardShare
        val aboutRow: LinearLayout = root.findViewById(R.id.group4)
        aboutRow.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About CardShare")
                .setMessage("CardShare v1.0\n\nSmart business card sharing for the modern professional.\n\n© 2024 CardShare Team")
                .setPositiveButton("OK", null)
                .show()
        }

        // Log Out
        val logoutRow: LinearLayout = root.findViewById(R.id.group5)
        logoutRow.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out") { _, _ ->
                    AppSession.logout(requireContext())
                    val intent = Intent(requireContext(), Start::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        _root?.let { populateHeader(it) }
    }

    private fun populateHeader(root: View) {
        val ctx = requireContext()
        val name = AppSession.getUserName(ctx).ifEmpty { "Your Name" }
        val email = AppSession.getEmail(ctx).ifEmpty { "Tap to edit profile" }
        root.findViewById<TextView>(R.id.settingsNameText).text = name
        root.findViewById<TextView>(R.id.settingsEmailText).text = email
        val localAvatar = AppSession.getLocalAvatar(ctx)
        if (localAvatar.isNotEmpty()) {
            try {
                val bytes = Base64.decode(localAvatar, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bmp != null) root.findViewById<ImageView>(R.id.imageView8).setImageBitmap(bmp)
            } catch (_: Exception) {}
        }
    }

    private fun showOrderDialog(statusFilter: String?) {
        val ctx = requireContext()
        val uid = AppSession.getUserId(ctx)
        val title = when (statusFilter) {
            "pending"   -> "In Delivery"
            "delivered" -> "After-sale"
            else        -> "All Orders"
        }

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

        val query = if (statusFilter != null)
            "SELECT * FROM orders WHERE uid = ? AND status = ? ORDER BY timestamp DESC"
        else
            "SELECT * FROM orders WHERE uid = ? ORDER BY timestamp DESC"
        val args = if (statusFilter != null) arrayOf(uid.toString(), statusFilter) else arrayOf(uid.toString())
        val cursor = db.rawQuery(query, args)

        val lines = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val cardName = cursor.getString(cursor.getColumnIndexOrThrow("card_name")) ?: ""
                val material = cursor.getString(cursor.getColumnIndexOrThrow("material")) ?: ""
                val qty = cursor.getInt(cursor.getColumnIndexOrThrow("qty"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status")) ?: "pending"
                val statusLabel = if (status == "delivered") "Delivered" else "In Delivery"
                lines.add("#$id  $cardName\n$material × $qty  ¥%.2f  [$statusLabel]".format(total))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        val message = if (lines.isEmpty()) "No orders yet.\n\nPlace your first order from the Wrench tab." else lines.joinToString("\n\n")

        AlertDialog.Builder(ctx)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
