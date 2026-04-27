package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.UserCardsResponse
import com.team4.cardcase2.interfaces.HttpRequest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Make1Fragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_make1, container, false)

        val myCardView: RecyclerView = root.findViewById(R.id.myCardView)
        myCardView.layoutManager = LinearLayoutManager(requireContext())

        val nextButton: Button = root.findViewById(R.id.gonext)
        nextButton.setOnClickListener {
            val bundle = Bundle().apply { putInt("cardId", selectedCardId) }
            findNavController().navigate(R.id.make2Fragment, bundle)
        }

        val ctx = requireContext()
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)
        if (userId > 0 && token.isNotEmpty()) {
            HttpRequest().sendGetRequest(
                "http://10.0.2.2:8080/api/cards/user/$userId", token
            ) { response, exception ->
                activity?.runOnUiThread {
                    if (exception == null && response != null) {
                        try {
                            val result = UserCardsResponse.fromJson(response)
                            if (result.success) {
                                val adapter = CardAdapter(result.cards) { card ->
                                    selectedCardId = card.cardId
                                }
                                myCardView.adapter = adapter
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }
            }
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Make1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
