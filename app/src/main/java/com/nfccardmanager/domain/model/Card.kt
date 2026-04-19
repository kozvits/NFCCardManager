package com.nfccardmanager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: Long = 0,
    val uid: String,
    val type: CardType,
    val data: ByteArray? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSelected: Boolean = false,
    val name: String = ""
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Card
        return id == other.id && uid == other.uid
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }
}

enum class CardType {
    MIFARE_CLASSIC_1K,
    MIFARE_CLASSIC_4K,
    MIFARE_ULTRALIGHT,
    UNKNOWN
}