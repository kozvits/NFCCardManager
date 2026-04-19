package com.nfccardmanager.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.usecase.DeleteCardUseCase
import com.nfccardmanager.domain.usecase.GetCardByIdUseCase
import com.nfccardmanager.domain.usecase.SelectCardUseCase
import com.nfccardmanager.util.HcePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val card: Card? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val selectCardUseCase: SelectCardUseCase,
    private val hcePreferences: HcePreferences
) : ViewModel() {

    private val cardId: Long = savedStateHandle.get<Long>("cardId") ?: -1

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCard()
    }

    private fun loadCard() {
        viewModelScope.launch {
            try {
                val card = getCardByIdUseCase(cardId)
                _uiState.value = DetailUiState(card = card, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = DetailUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteCard() {
        viewModelScope.launch {
            try {
                deleteCardUseCase(cardId)
                _uiState.value = _uiState.value.copy(isDeleted = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun selectForEmulation() {
        viewModelScope.launch {
            try {
                selectCardUseCase(cardId)
                _uiState.value.card?.let { card ->
                    hcePreferences.setUid(card.uid)
                    hcePreferences.setCardData(card.data)
                    hcePreferences.setCardType(card.type.name)
                    hcePreferences.setEmulationActive(true)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}