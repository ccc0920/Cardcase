package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.CardLocalStore
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.*
import com.team4.cardcase2.interfaces.HttpRequest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PayFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var selectedCardId: Int = -1

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
        val root = inflater.inflate(R.layout.fragment_pay, container, false)

        // Material options
        val materialsSpinner: Spinner = root.findViewById(R.id.spinner)
        val materials = arrayOf("标准名片(铜版纸)", "高级名片(特种纸)", "豪华名片(烫金)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, materials)
        materialsSpinner.adapter = adapter

        // Quantity
        val quantityInput: EditText = root.findViewById(R.id.editTextTextPersonName2)
        quantityInput.setText("100")

        // Contact info
        val nameInput: EditText = root.findViewById(R.id.editTextTextPersonName3)
        val phoneInput: EditText = root.findViewById(R.id.editTextTextPersonName4)
        val addressInput: EditText = root.findViewById(R.id.editTextTextPersonName5)

        // Load selected card info — own cards from SQLite immediately
        val ctx = requireContext()
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)

        val localCards = CardLocalStore.loadAll(ctx, userId)
        if (localCards.isNotEmpty()) {
            selectedCardId = localCards[0].cardId
            val name = localCards[0].elements.firstOrNull { it.type == "name" }?.content ?: ""
            nameInput.setText(name)
        }

        // Sync with server in background
        if (userId > 0 && token.isNotEmpty()) {
            HttpRequest().sendGetRequest(
                "http://10.0.2.2:8080/api/cards/user/$userId", token
            ) { response, exception ->
                activity?.runOnUiThread {
                    if (exception == null && response != null) {
                        try {
                            val result = UserCardsResponse.fromJson(response)
                            if (result.success && result.cards.isNotEmpty()) {
                                CardLocalStore.syncFromServer(ctx, userId, result.cards)
                                // Only update pre-fill if nothing was selected yet
                                if (selectedCardId <= 0) {
                                    val synced = CardLocalStore.loadAll(ctx, userId)
                                    if (synced.isNotEmpty()) {
                                        selectedCardId = synced[0].cardId
                                        val name = synced[0].elements.firstOrNull { it.type == "name" }?.content ?: ""
                                        nameInput.setText(name)
                                    }
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }

        val completeButton: Button = root.findViewById(R.id.complete)
        completeButton.text = "确认下单"
        completeButton.setOnClickListener {
            val materials = materialsSpinner.selectedItem.toString()
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 100
            val contactInfo = """
                {"name":"${nameInput.text}","phone":"${phoneInput.text}","address":"${addressInput.text}"}
            """.trimIndent()
            val paymentMethod = """
                {"type":"alipay","status":"paid"}
            """.trimIndent()

            if (selectedCardId > 0 && userId > 0) {
                val orderRequest = """
                    {"userId":$userId,"cardId":$selectedCardId,"materials":"$materials","quantity":$quantity,"contactInfo":$contactInfo,"paymentMethod":$paymentMethod}
                """.trimIndent()

                val http = HttpRequest()
                http.sendPostRequest("http://10.0.2.2:8080/api/orders", token, orderRequest) { response, exception ->
                    activity?.runOnUiThread {
                        if (exception != null) {
                            Toast.makeText(ctx, "下单失败: ${exception.message}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, "下单成功!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.wrench1Fragment)
                        }
                    }
                }
            } else {
                Toast.makeText(ctx, "请先选择名片", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}