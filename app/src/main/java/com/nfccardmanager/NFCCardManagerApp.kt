package com.nfccardmanager

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NFCCardManagerApp : Application() {
    override fun onCreate() {
        try {
            super.onCreate()
        } catch (e: Exception) {
            Log.e("NFCCardManagerApp", "Error in onCreate", e)
        }
    }
}