package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.CardLocalStore
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.ServerCard
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

        root.findViewById<android.widget.Button>(R.id.addButton2).setOnClickListener {
            findNavController().navigate(R.id.createNewFragment)
        }

        root.findViewById<ImageButton>(R.id.scanButton2).setOnClickListener {
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
        val uid = AppSession.getUserId(ctx)

        // Always show from local SQLite immediately — no network needed
        renderCards(CardLocalStore.loadAll(ctx, uid))

        val token = AppSession.getToken(ctx)
        if (uid == 0 || token.isEmpty()) return

        // Sync with server in the background
        HttpRequest().sendGetRequest("http://10.0.2.2:8080/api/cards/user/$uid", token) { response, exception ->
            if (exception != null || response == null) return@sendGetRequest
            activity?.runOnUiThread {
                try {
                    val result = UserCardsResponse.fromJson(response)
                    if (result.success && result.cards.isNotEmpty()) {
                        CardLocalStore.syncFromServer(ctx, uid, result.cards)
                        renderCards(CardLocalStore.loadAll(ctx, uid))
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun renderCards(cards: List<ServerCard>) {
        cardAdapter = CardAdapter(cards) { card ->
            val cardName = card.elements.firstOrNull { it.type == "name" }?.content ?: "Card"
            AlertDialog.Builder(requireContext())
                .setTitle(cardName)
                .setItems(arrayOf("Edit", "Delete")) { _, which ->
                    if (which == 0) {
                        findNavController().navigate(R.id.createNewFragment,
                            Bundle().apply { putInt("cardId", card.cardId) })
                    } else {
                        confirmDelete(card)
                    }
                }
                .show()
        }
        myCardView.adapter = cardAdapter
    }

    private fun confirmDelete(card: ServerCard) {
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("Delete Card")
            .setMessage("Delete this card? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val uid = AppSession.getUserId(ctx)
                CardLocalStore.delete(ctx, card.cardId)
                renderCards(CardLocalStore.loadAll(ctx, uid))
                val token = AppSession.getToken(ctx)
                if (token.isNotEmpty()) {
                    HttpRequest().sendDeleteRequest(
                        "http://10.0.2.2:8080/api/cards/${card.cardId}", token) { _, _ -> }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onQRCodeScanned(result: String) {
        parentFragmentManager.popBackStack()
    }
}
