package com.nfccardmanager.presentation.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nfccardmanager.R
import com.nfccardmanager.service.CardEmulationService
import com.nfccardmanager.util.HcePreferences

class CardDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        val cardId = intent.getLongExtra("CARD_ID", 0L)
        
        // Header
        val backButton: Button = findViewById(R.id.back_button)
        val title: TextView = findViewById(R.id.title)
        
        // Card info
        val cardName: TextView = findViewById(R.id.card_name)
        val cardUid: TextView = findViewById(R.id.card_uid)
        val cardType: TextView = findViewById(R.id.card_type)
        val description: TextView = findViewById(R.id.description)
        
        // Buttons
        val emulationButton: Button = findViewById(R.id.emulation_button)
        val deleteButton: Button = findViewById(R.id.delete_button)
        
        // Set demo data
        title.text = "Card Details"
        
        if (cardId == 1L) {
            cardName.text = "Office Access Card"
            cardUid.text = "UID: E00401500000B102"
            cardType.text = "Type: Mifare Classic 1K"
            description.text = "Office building access card\nLast used: Today"
        } else if (cardId == 2L) {
            cardName.text = "Metro Card"
            cardUid.text = "UID: A1B2C3D4E5F67788"
            cardType.text = "Type: Mifare Ultralight"
            description.text = "Public transport card\nBalance: $5.00"
        } else {
            cardName.text = "Test Card"
            cardUid.text = "UID: 1234567890ABCDEF"
            cardType.text = "Type: Generic NFC"
            description.text = "Testing card for development"
        }
        
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Emulation button
        emulationButton.setOnClickListener {
            // Save selected card ID for emulation
            HcePreferences.setSelectedCardId(this, cardId)
            
            // Try to start emulation service
            try {
                CardEmulationService.startService(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            finish() // Return to main screen
        }
        
        // Delete button
        deleteButton.setOnClickListener {
            // TODO: Implement delete functionality
            finish()
        }
    }
}