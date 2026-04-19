package com.nfccardmanager.domain.usecase

import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCardsUseCase @Inject constructor(
    private val repository: CardRepository
) {
    operator fun invoke(): Flow<List<Card>> = repository.getAllCards()
}

class GetCardByIdUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(id: Long): Card? = repository.getCardById(id)
}

class GetSelectedCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(): Card? = repository.getSelectedCard()
}

class SaveCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(card: Card): Long {
        return repository.insertCard(card)
    }
}

class DeleteCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteCardById(id)
    }
}

class SelectCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.selectCard(id)
    }
}

class CheckCardExistsUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(uid: String): Boolean {
        return repository.cardExists(uid)
    }
}