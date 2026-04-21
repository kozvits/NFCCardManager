package com.nfccardmanager.presentation.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.nfccardmanager.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Простая задержка для демонстрации (200мс)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, com.nfccardmanager.presentation.ui.main.MainActivity::class.java))
            finish()
        }, 200)
    }
}