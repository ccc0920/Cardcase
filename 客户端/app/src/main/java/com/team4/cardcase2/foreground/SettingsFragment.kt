package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.Start

class SettingsFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        // Edit profile button
        val editProfileButton: ImageButton = root.findViewById(R.id.imageView9)
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.infoFragment)
        }

        // Orders — open Taobao business card search
        val taobaoUrl = "https://s.taobao.com/search?q=%E5%90%8D%E7%89%87%E5%8D%B0%E5%88%B7"
        val orderAllRow: LinearLayout = root.findViewById(R.id.orderAllRow)
        orderAllRow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(taobaoUrl)))
        }
        val orderDeliveryRow: LinearLayout = root.findViewById(R.id.orderDeliveryRow)
        orderDeliveryRow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(taobaoUrl)))
        }
        val orderAftersaleRow: LinearLayout = root.findViewById(R.id.orderAftersaleRow)
        orderAftersaleRow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(taobaoUrl)))
        }

        // Contact Us
        val contactRow: LinearLayout = root.findViewById(R.id.group2)
        contactRow.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@cardcase.app")
                putExtra(Intent.EXTRA_SUBJECT, "CardCase Support")
            }
            startActivity(Intent.createChooser(intent, "Contact Us"))
        }

        // Privacy & Security
        val privacyRow: LinearLayout = root.findViewById(R.id.group3)
        privacyRow.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cardcase.app/privacy"))
            startActivity(intent)
        }

        // About CardCase
        val aboutRow: LinearLayout = root.findViewById(R.id.group4)
        aboutRow.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About CardCase")
                .setMessage("CardCase v1.0\n\nSmart business card sharing for the modern professional.\n\n© 2024 CardCase Team")
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
