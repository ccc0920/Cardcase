package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.UserCardsResponse
import com.team4.cardcase2.interfaces.HttpRequest

class BlankFragment : Fragment(), ScanFragment.QRCodeScanResultListener {

    private lateinit var myCardView: RecyclerView
    private var cardAdapter: CardAdapter? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_blank, container, false)

        myCardView = root.findViewById(R.id.myCardView)
        myCardView.layoutManager = LinearLayoutManager(requireContext())

        val addButton: Button = root.findViewById(R.id.addButton2)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.createCardFragment)
        }

        val scanButton: Button = root.findViewById(R.id.scanButton2)
        scanButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.blankFragment, ScanFragment())
                .addToBackStack(null)
                .commit()
        }

        loadCards()

        return root
    }

    override fun onResume() {
        super.onResume()
        loadCards()
    }

    private fun loadCards() {
        val ctx = context ?: return
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)
        if (userId == 0 || token.isEmpty()) return

        val url = "http://10.0.2.2:8080/api/cards/user/$userId"
        HttpRequest().sendGetRequest(url, token) { response, exception ->
            activity?.runOnUiThread {
                if (exception != null) {
                    Toast.makeText(ctx, "Failed to load cards", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
                try {
                    val result = UserCardsResponse.fromJson(response!!)
                    if (result.success) {
                        cardAdapter = CardAdapter(result.cards) { card ->
                            val bundle = Bundle().apply { putInt("cardId", card.cardId) }
                            findNavController().navigate(R.id.cardDetailFragment, bundle)
                        }
                        myCardView.adapter = cardAdapter
                    }
                } catch (e: Exception) {
                    Toast.makeText(ctx, "Error parsing cards", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onQRCodeScanned(result: String) {
        parentFragmentManager.popBackStack()
    }
}
