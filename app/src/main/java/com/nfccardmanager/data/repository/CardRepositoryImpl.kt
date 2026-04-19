package com.nfccardmanager.data.repository

import com.nfccardmanager.data.local.dao.CardDao
import com.nfccardmanager.data.local.entity.CardEntity
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCardById(id: Long): Card? {
        return cardDao.getCardById(id)?.toDomain()
    }

    override suspend fun getCardByUid(uid: String): Card? {
        return cardDao.getCardByUid(uid)?.toDomain()
    }

    override suspend fun getSelectedCard(): Card? {
        return cardDao.getSelectedCard()?.toDomain()
    }

    override suspend fun cardExists(uid: String): Boolean {
        return cardDao.cardExists(uid)
    }

    override suspend fun insertCard(card: Card): Long {
        return cardDao.insertCard(CardEntity.fromDomain(card))
    }

    override suspend fun updateCard(card: Card) {
        cardDao.updateCard(CardEntity.fromDomain(card))
    }

    override suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(CardEntity.fromDomain(card))
    }

    override suspend fun deleteCardById(id: Long) {
        cardDao.deleteCardById(id)
    }

    override suspend fun selectCard(id: Long) {
        cardDao.deselectAllCards()
        cardDao.selectCard(id)
    }

    override suspend fun deselectCard(id: Long) {
        cardDao.deselectCard(id)
    }
}