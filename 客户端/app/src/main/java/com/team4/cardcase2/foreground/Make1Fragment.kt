package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.CardLocalStore
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.Elements
import com.team4.cardcase2.entity.ServerCard
import com.team4.cardcase2.entity.UserCardsResponse
import com.team4.cardcase2.interfaces.HttpRequest

class Make1Fragment : Fragment() {

    private var selectedCardId: Int = -1
    private var selectedCardName: String = ""

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
            if (selectedCardId <= 0) {
                Toast.makeText(requireContext(), "Please select a card first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putInt("cardId", selectedCardId)
                putString("cardName", selectedCardName)
            }
            findNavController().navigate(R.id.make2Fragment, bundle)
        }

        val ctx = requireContext()
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)

        // Show own cards from SQLite immediately
        fun showCards(cards: List<ServerCard>) {
            myCardView.adapter = SelectableCardAdapter(cards) { card, name ->
                selectedCardId = card.cardId
                selectedCardName = name
            }
        }
        showCards(CardLocalStore.loadAll(ctx, userId))

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
                                showCards(CardLocalStore.loadAll(ctx, userId))
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }

        return root
    }

    private inner class SelectableCardAdapter(
        private val cards: List<ServerCard>,
        private val onSelect: (ServerCard, String) -> Unit
    ) : RecyclerView.Adapter<SelectableCardAdapter.VH>() {

        var selectedPosition = -1

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.cardRowName)
            val company: TextView = view.findViewById(R.id.cardRowCompany)
            val radio: RadioButton = view.findViewById(R.id.cardRowRadio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_card, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val card = cards[position]
            val name = card.elements.getByType("name").ifEmpty { "Unnamed Card" }
            val company = card.elements.getByType("company")
            holder.name.text = name
            holder.company.text = company
            holder.radio.isChecked = selectedPosition == position
            holder.itemView.setOnClickListener {
                val old = selectedPosition
                selectedPosition = position
                notifyItemChanged(old)
                notifyItemChanged(position)
                onSelect(card, name)
            }
        }

        override fun getItemCount() = cards.size

        private fun List<Elements>.getByType(type: String) =
            firstOrNull { it.type == type }?.content ?: ""
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = Make1Fragment()
    }
}
