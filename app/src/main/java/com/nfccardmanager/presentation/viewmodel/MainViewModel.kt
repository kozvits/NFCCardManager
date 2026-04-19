package com.nfccardmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.usecase.DeleteCardUseCase
import com.nfccardmanager.domain.usecase.GetAllCardsUseCase
import com.nfccardmanager.domain.usecase.SelectCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val cards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val selectCardUseCase: SelectCardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            getAllCardsUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { cards ->
                    _uiState.value = MainUiState(
                        cards = cards,
                        isLoading = false
                    )
                }
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            try {
                deleteCardUseCase(card.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun selectCard(card: Card) {
        viewModelScope.launch {
            try {
                selectCardUseCase(card.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}