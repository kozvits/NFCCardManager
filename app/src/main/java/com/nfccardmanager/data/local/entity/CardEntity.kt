package com.nfccardmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.model.CardType

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uid: String,
    val type: String,
    val data: ByteArray?,
    val createdAt: Long,
    val isSelected: Boolean,
    val name: String
) {
    fun toDomain(): Card = Card(
        id = id,
        uid = uid,
        type = try { CardType.valueOf(type) } catch (e: Exception) { CardType.UNKNOWN },
        data = data,
        createdAt = createdAt,
        isSelected = isSelected,
        name = name
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CardEntity
        return id == other.id && uid == other.uid
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }

    companion object {
        fun fromDomain(card: Card): CardEntity = CardEntity(
            id = card.id,
            uid = card.uid,
            type = card.type.name,
            data = card.data,
            createdAt = card.createdAt,
            isSelected = card.isSelected,
            name = card.name
        )
    }
}