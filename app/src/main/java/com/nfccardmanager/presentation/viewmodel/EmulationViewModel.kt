package com.nfccardmanager.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.usecase.GetSelectedCardUseCase
import com.nfccardmanager.util.HcePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmulationUiState(
    val card: Card? = null,
    val isEmulating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmulationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSelectedCardUseCase: GetSelectedCardUseCase,
    private val hcePreferences: HcePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmulationUiState())
    val uiState: StateFlow<EmulationUiState> = _uiState.asStateFlow()

    init {
        loadSelectedCard()
    }

    private fun loadSelectedCard() {
        viewModelScope.launch {
            try {
                val card = getSelectedCardUseCase()
                val isEmulating = hcePreferences.isEmulationActive()
                _uiState.value = EmulationUiState(
                    card = card,
                    isEmulating = isEmulating
                )
            } catch (e: Exception) {
                _uiState.value = EmulationUiState(error = e.message)
            }
        }
    }

    fun startEmulation() {
        viewModelScope.launch {
            _uiState.value.card?.let { card ->
                hcePreferences.setUid(card.uid)
                hcePreferences.setCardData(card.data)
                hcePreferences.setCardType(card.type.name)
                hcePreferences.setEmulationActive(true)
                _uiState.value = _uiState.value.copy(isEmulating = true)
            }
        }
    }

    fun stopEmulation() {
        hcePreferences.clearEmulation()
        _uiState.value = _uiState.value.copy(isEmulating = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}