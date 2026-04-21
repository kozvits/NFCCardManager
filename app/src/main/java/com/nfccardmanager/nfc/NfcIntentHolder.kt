package com.nfccardmanager.nfc

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcIntentHolder @Inject constructor() {
    private val _lastIntent = MutableStateFlow<Intent?>(null)
    val lastIntent: StateFlow<Intent?> = _lastIntent.asStateFlow()

    // Convenience property used by MainActivity
    var intent: Intent?
        get() = _lastIntent.value
        set(value) {
            if (value != null) {
                _lastIntent.value = value
            }
        }

    fun updateIntent(intent: Intent) {
        _lastIntent.value = intent
    }

    fun clearIntent() {
        _lastIntent.value = null
    }

    fun consumeIntent(): Intent? {
        val intent = _lastIntent.value
        _lastIntent.value = null
        return intent
    }
}
