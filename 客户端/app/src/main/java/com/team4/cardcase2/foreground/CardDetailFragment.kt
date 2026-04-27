package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.Elements
import com.team4.cardcase2.entity.Encoder
import com.team4.cardcase2.entity.QRCodeGenerator
import com.team4.cardcase2.entity.WholeServerCard
import com.team4.cardcase2.interfaces.HttpRequest

class CardDetailFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_card_detail, container, false)

        val showName: TextView = root.findViewById(R.id.showName)
        val showCompany: TextView = root.findViewById(R.id.showCompany)
        val showPhone: TextView = root.findViewById(R.id.showPhone)
        val showEmail: TextView = root.findViewById(R.id.showEmail)
        val showName2: TextView = root.findViewById(R.id.showName2)
        val showEmail2: TextView = root.findViewById(R.id.showEmail2)
        val showPhone2: TextView = root.findViewById(R.id.showPhone2)
        val showAddress2: TextView = root.findViewById(R.id.showAddress2)
        val showWechat: TextView = root.findViewById(R.id.showWechat)
        val qrView: ImageView = root.findViewById(R.id.qrView)
        val qrButton: Button = root.findViewById(R.id.qrButton)
        val editButton: Button = root.findViewById(R.id.editButton)
        val backButton: TextView = root.findViewById(R.id.backButton)

        qrView.visibility = View.GONE

        val cardId = arguments?.getInt("cardId", 0) ?: 0

        backButton.setOnClickListener {
            findNavController().navigate(R.id.blankFragment)
        }

        editButton.setOnClickListener {
            val bundle = Bundle().apply { putInt("cardId", cardId) }
            findNavController().navigate(R.id.action_cardDetailFragment_to_createNewFragment, bundle)
        }

        if (cardId > 0) {
            val ctx = requireContext()
            val token = AppSession.getToken(ctx)
            HttpRequest().sendGetRequest("http://10.0.2.2:8080/api/cards/$cardId", token) { response, exception ->
                activity?.runOnUiThread {
                    if (exception != null || response == null) {
                        Toast.makeText(ctx, "Failed to load card", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }
                    try {
                        val result = WholeServerCard.fromJson(response)
                        val card = result.card
                        val name = card.elements.getByType("name")
                        val title = card.elements.getByType("title")
                        val company = card.elements.getByType("company")
                        val phone = card.elements.getByType("phone")
                        val email = card.elements.getByType("email")
                        val wechat = card.elements.getByType("wechat")

                        showName.text = name
                        showCompany.text = if (title.isNotEmpty()) "$title · $company" else company
                        showPhone.text = phone.ifEmpty { "—" }
                        showEmail.text = email.ifEmpty { "—" }
                        showName2.text = name
                        showEmail2.text = email.ifEmpty { "—" }
                        showPhone2.text = phone.ifEmpty { "—" }
                        showAddress2.text = card.elements.getByType("address").ifEmpty { "—" }
                        showWechat.text = wechat.ifEmpty { "—" }

                        val qrBitmap = QRCodeGenerator().generateQRCode(
                            Encoder().encode(card.cardId), 300, 300
                        )
                        qrView.setImageBitmap(qrBitmap)
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Error loading card", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        qrButton.setOnClickListener {
            qrView.visibility = if (qrView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        return root
    }

    private fun List<Elements>.getByType(type: String): String =
        firstOrNull { it.type == type }?.content ?: ""

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CardDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
