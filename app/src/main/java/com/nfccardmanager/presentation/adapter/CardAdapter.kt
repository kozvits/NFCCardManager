package com.nfccardmanager.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nfccardmanager.R
import com.nfccardmanager.data.local.entity.CardEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardAdapter(
    private var cards: List<CardEntity>
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onCardClick: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
        holder.itemView.setOnClickListener {
            onCardClick?.invoke(card.id)
        }
    }

    override fun getItemCount(): Int = cards.size

    fun updateCards(newCards: List<CardEntity>) {
        cards = newCards
        notifyDataSetChanged()
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardName: TextView = itemView.findViewById(R.id.card_name)
        private val cardType: TextView = itemView.findViewById(R.id.card_type)
        private val cardUid: TextView = itemView.findViewById(R.id.card_uid)
        private val scanDate: TextView = itemView.findViewById(R.id.scan_date)

        fun bind(card: CardEntity) {
            cardName.text = card.name ?: "Unnamed Card"
            cardType.text = card.type ?: "Unknown Type"
            
            // Format UID
            val uid = card.uid ?: "0000000000000000"
            cardUid.text = "UID: ${formatUid(uid)}"
            
            // Format date
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            scanDate.text = "Scanned: ${dateFormat.format(Date(card.scanDate))}"
        }

        private fun formatUid(uid: String): String {
            return if (uid.length >= 8) {
                "${uid.substring(0, 8)}..."
            } else {
                uid
            }
        }
    }
}