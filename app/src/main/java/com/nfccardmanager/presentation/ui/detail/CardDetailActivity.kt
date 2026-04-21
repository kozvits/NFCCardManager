package com.nfccardmanager.presentation.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Legacy stub — navigation is now handled entirely via Compose NavHost in MainActivity.
 * This activity is kept only to satisfy the manifest; it immediately finishes.
 */
class CardDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
