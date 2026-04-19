package com.nfccardmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nfccardmanager.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE uid = :uid")
    suspend fun getCardByUid(uid: String): CardEntity?

    @Query("SELECT * FROM cards WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedCard(): CardEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM cards WHERE uid = :uid)")
    suspend fun cardExists(uid: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("UPDATE cards SET isSelected = 0")
    suspend fun deselectAllCards()

    @Query("UPDATE cards SET isSelected = 1 WHERE id = :id")
    suspend fun selectCard(id: Long)

    @Query("UPDATE cards SET isSelected = 0 WHERE id = :id")
    suspend fun deselectCard(id: Long)
}