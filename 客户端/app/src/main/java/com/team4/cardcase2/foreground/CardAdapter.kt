package com.team4.cardcase2.foreground

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        // Apply color gradient to card front background
        val gradientRes = when (card.design.color) {
            "purple" -> R.drawable.card_gradient_purple
            "teal"   -> R.drawable.card_gradient_teal
            "rose"   -> R.drawable.card_gradient_rose
            "slate"  -> R.drawable.card_gradient_slate
            "white"  -> R.drawable.card_gradient_white
            else     -> R.drawable.card_gradient_blue
        }
        holder.cardFront.background = ContextCompat.getDrawable(holder.itemView.context, gradientRes)

        // White theme uses dark text/icons; all others use white
        val isWhite = card.design.color == "white"
        val nameColor   = if (isWhite) Color.parseColor("#1E293B") else Color.WHITE
        val subColor    = if (isWhite) Color.parseColor("#64748B") else Color.parseColor("#C7D2FE")
        val detailColor = if (isWhite) Color.parseColor("#334155") else Color.WHITE
        holder.showName.setTextColor(nameColor)
        holder.showTitle.setTextColor(subColor)
        holder.showCompany.setTextColor(subColor)
        holder.showPhone.setTextColor(detailColor)
        holder.showEmail.setTextColor(detailColor)
        holder.iconCompany.setColorFilter(subColor)
        holder.iconPhone.setColorFilter(detailColor)
        holder.iconEmail.setColorFilter(detailColor)

        // Avatar: decode Base64 if present, else show default icon
        if (card.avatar.isNotEmpty()) {
            try {
                val bytes = Base64.decode(card.avatar, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bmp != null) holder.cardAvatar.setImageBitmap(bmp)
                else holder.cardAvatar.setImageResource(R.drawable.avatar_default)
            } catch (e: Exception) {
                holder.cardAvatar.setImageResource(R.drawable.avatar_default)
            }
        } else {
            holder.cardAvatar.setImageResource(R.drawable.avatar_default)
        }

        val qrBitmap = qrGenerator.generateQRCode(encoder.encode(card.cardId), 300, 300)
        holder.qrView.setImageBitmap(qrBitmap)

        val isFlipped = flippedCards.contains(card.cardId)
        holder.cardFront.visibility = if (isFlipped) View.INVISIBLE else View.VISIBLE
        holder.cardBack.visibility = if (isFlipped) View.VISIBLE else View.INVISIBLE
        holder.cardFront.rotationY = 0f
        holder.cardBack.rotationY = 0f

        // Listeners on faces directly — they fill itemView so touches land here first
        holder.cardFront.setOnClickListener {
            flippedCards.add(card.cardId)
            flipToBack(holder)
        }
        holder.cardFront.setOnLongClickListener {
            onLongPress?.invoke(card)
            true
        }
        holder.cardBack.setOnClickListener {
            flippedCards.remove(card.cardId)
            flipToFront(holder)
        }
        holder.cardBack.setOnLongClickListener {
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
        val cardAvatar: ImageView = itemView.findViewById(R.id.cardAvatar)
        val iconCompany: ImageView = itemView.findViewById(R.id.iconCompany)
        val iconPhone: ImageView = itemView.findViewById(R.id.iconPhone)
        val iconEmail: ImageView = itemView.findViewById(R.id.iconEmail)
    }

    private fun List<Elements>.getByType(type: String): String =
        firstOrNull { it.type == type }?.content ?: ""
}
