package com.nfccardmanager.domain.repository

import com.nfccardmanager.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getAllCards(): Flow<List<Card>>
    suspend fun getCardById(id: Long): Card?
    suspend fun getCardByUid(uid: String): Card?
    suspend fun getSelectedCard(): Card?
    suspend fun cardExists(uid: String): Boolean
    suspend fun insertCard(card: Card): Long
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    suspend fun deleteCardById(id: Long)
    suspend fun selectCard(id: Long)
    suspend fun deselectCard(id: Long)
}