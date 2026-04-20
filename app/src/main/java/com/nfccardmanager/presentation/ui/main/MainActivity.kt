package com.nfccardmanager.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nfccardmanager.R
import com.nfccardmanager.data.local.entity.CardEntity
import com.nfccardmanager.presentation.adapter.CardAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    
    @Inject
    lateinit var cardRepository: com.nfccardmanager.data.repository.CardRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        recyclerView = findViewById(R.id.card_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Create dummy cards for testing
        val dummyCards = listOf(
            // Office NFC card
            CardEntity(
                id = 1,
                uid = "E00401500000B102",
                type = "Mifare Classic 1K",
                name = "Office Access Card",
                data = null,
                createdAt = System.currentTimeMillis(),
                isSelected = false
            ),
            // Metro card
            CardEntity(
                id = 2,
                uid = "A1B2C3D4E5F67788",
                type = "Mifare Ultralight",
                name = "Metro Card",
                data = null,
                createdAt = System.currentTimeMillis() - 86400000,
                isSelected = false
            ),
            // Test card
            CardEntity(
                id = 3,
                uid = "1234567890ABCDEF",
                type = "Generic NFC",
                name = "Test Card",
                data = null,
                createdAt = System.currentTimeMillis() - 172800000,
                isSelected = false
            )
        )
        
        adapter = CardAdapter(dummyCards)
        adapter.onCardClick = { cardId ->
            // Navigate to detail screen
            val intent = Intent(this, CardDetailActivity::class.java).apply {
                putExtra("CARD_ID", cardId)
            }
            startActivity(intent)
        }
        
        recyclerView.adapter = adapter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}