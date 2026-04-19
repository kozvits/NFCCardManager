package com.nfccardmanager.presentation.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.usecase.CheckCardExistsUseCase
import com.nfccardmanager.domain.usecase.SaveCardUseCase
import com.nfccardmanager.nfc.NfcCardReader
import com.nfccardmanager.nfc.NfcReadResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    data class Success(val card: Card) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
    object Duplicate : ScanUiState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val nfcCardReader: NfcCardReader,
    private val saveCardUseCase: SaveCardUseCase,
    private val checkCardExistsUseCase: CheckCardExistsUseCase,
    private val nfcIntentHolder: com.nfccardmanager.nfc.NfcIntentHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun startScanning() {
        _uiState.value = ScanUiState.Scanning
    }

    fun processStoredNfcIntent() {
        viewModelScope.launch {
            val intent = nfcIntentHolder.consumeIntent()
            if (intent != null) {
                processNfcIntent(intent)
            }
        }
    }

    fun processNfcIntent(intent: Intent?) {
        viewModelScope.launch {
            _uiState.value = ScanUiState.Scanning
            when (val result = nfcCardReader.readCard(intent)) {
                is NfcReadResult.Success -> {
                    val exists = checkCardExistsUseCase(result.card.uid)
                    if (exists) {
                        _uiState.value = ScanUiState.Duplicate
                    } else {
                        _uiState.value = ScanUiState.Success(result.card)
                    }
                }
                is NfcReadResult.Error -> {
                    _uiState.value = ScanUiState.Error(result.message)
                }
                is NfcReadResult.NoNfc -> {
                    _uiState.value = ScanUiState.Error("No NFC available")
                }
                is NfcReadResult.NfcDisabled -> {
                    _uiState.value = ScanUiState.Error("NFC is disabled")
                }
            }
        }
    }

    fun saveCard(name: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ScanUiState.Success) {
                try {
                    val cardToSave = currentState.card.copy(name = name.ifEmpty { "Card ${currentState.card.uid.take(8)}" })
                    saveCardUseCase(cardToSave)
                    _uiState.value = ScanUiState.Idle
                } catch (e: Exception) {
                    _uiState.value = ScanUiState.Error(e.message ?: "Failed to save card")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = ScanUiState.Idle
    }
}