package com.team4.cardcase2.foreground

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.Elements
import com.team4.cardcase2.entity.QRCodeGenerator
import com.team4.cardcase2.entity.Encoder
import com.team4.cardcase2.entity.ServerCard

class CardAdapter(
    private val cards: List<ServerCard>,
    private val onLongPress: ((ServerCard) -> Unit)? = null
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val flippedCards = mutableSetOf<Int>()
    private val qrGenerator = QRCodeGenerator()
    private val encoder = Encoder()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.server_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]

        holder.showName.text = card.elements.getByType("name")
        holder.showTitle.text = card.elements.getByType("title")
        holder.showCompany.text = card.elements.getByType("company").uppercase()
        holder.showPhone.text = card.elements.getByType("phone")
        holder.showEmail.text = card.elements.getByType("email")

        val qrBitmap = qrGenerator.generateQRCode(encoder.encode(card.cardId), 300, 300)
        holder.qrView.setImageBitmap(qrBitmap)

        val isFlipped = flippedCards.contains(card.cardId)
        holder.cardFront.visibility = if (isFlipped) View.INVISIBLE else View.VISIBLE
        holder.cardBack.visibility = if (isFlipped) View.VISIBLE else View.INVISIBLE
        holder.cardFront.rotationY = 0f
        holder.cardBack.rotationY = 0f

        holder.itemView.setOnClickListener {
            if (flippedCards.contains(card.cardId)) {
                flippedCards.remove(card.cardId)
                flipToFront(holder)
            } else {
                flippedCards.add(card.cardId)
                flipToBack(holder)
            }
        }

        holder.itemView.setOnLongClickListener {
            onLongPress?.invoke(card)
            true
        }
    }

    override fun getItemCount(): Int = cards.size

    private fun flipToBack(holder: CardViewHolder) {
        val scale = holder.itemView.resources.displayMetrics.density
        holder.cardFront.cameraDistance = 8000 * scale
        holder.cardBack.cameraDistance = 8000 * scale

        ObjectAnimator.ofFloat(holder.cardFront, "rotationY", 0f, 90f).apply {
            duration = 200
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    holder.cardFront.visibility = View.INVISIBLE
                    holder.cardBack.visibility = View.VISIBLE
                    holder.cardBack.rotationY = -90f
                    ObjectAnimator.ofFloat(holder.cardBack, "rotationY", -90f, 0f).apply {
                        duration = 200
                        start()
                    }
                }
            })
            start()
        }
    }

    private fun flipToFront(holder: CardViewHolder) {
        val scale = holder.itemView.resources.displayMetrics.density
        holder.cardFront.cameraDistance = 8000 * scale
        holder.cardBack.cameraDistance = 8000 * scale

        ObjectAnimator.ofFloat(holder.cardBack, "rotationY", 0f, 90f).apply {
            duration = 200
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    holder.cardBack.visibility = View.INVISIBLE
                    holder.cardFront.visibility = View.VISIBLE
                    holder.cardFront.rotationY = -90f
                    ObjectAnimator.ofFloat(holder.cardFront, "rotationY", -90f, 0f).apply {
                        duration = 200
                        start()
                    }
                }
            })
            start()
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardFront: View = itemView.findViewById(R.id.cardFront)
        val cardBack: View = itemView.findViewById(R.id.cardBack)
        val showName: TextView = itemView.findViewById(R.id.showName)
        val showTitle: TextView = itemView.findViewById(R.id.showTitle)
        val showCompany: TextView = itemView.findViewById(R.id.showCompany)
        val showPhone: TextView = itemView.findViewById(R.id.showPhone)
        val showEmail: TextView = itemView.findViewById(R.id.showEmail)
        val qrView: ImageView = itemView.findViewById(R.id.qrView)
    }

    private fun List<Elements>.getByType(type: String): String =
        firstOrNull { it.type == type }?.content ?: ""
}
