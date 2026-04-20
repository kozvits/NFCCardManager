package com.nfccardmanager.nfc

import android.content.Intent
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Build
import com.nfccardmanager.domain.model.Card
import com.nfccardmanager.domain.model.CardType
import com.nfccardmanager.util.toHexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed class NfcReadResult {
    data class Success(val card: Card) : NfcReadResult()
    data class Error(val message: String) : NfcReadResult()
    object NoNfc : NfcReadResult()
    object NfcDisabled : NfcReadResult()
}

@Singleton
class NfcCardReader @Inject constructor() {

    suspend fun readCard(intent: Intent?): NfcReadResult = withContext(Dispatchers.IO) {
        if (intent == null) {
            return@withContext NfcReadResult.NoNfc
        }

        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcConstants.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcConstants.EXTRA_TAG)
        }

        if (tag == null) {
            return@withContext NfcReadResult.Error("No NFC tag found")
        }

        val uid = tag.id.toHexString()
        val cardType = detectCardType(tag)

        try {
            val cardData = when (cardType) {
                CardType.MIFARE_CLASSIC_1K, CardType.MIFARE_CLASSIC_4K -> readMifareClassic(tag)
                CardType.MIFARE_ULTRALIGHT -> readMifareUltralight(tag)
                else -> null
            }

            val card = Card(
                uid = uid,
                type = cardType,
                data = cardData,
                name = ""
            )
            NfcReadResult.Success(card)
        } catch (e: IOException) {
            val card = Card(
                uid = uid,
                type = cardType,
                data = null,
                name = ""
            )
            NfcReadResult.Success(card)
        } catch (e: Exception) {
            val card = Card(
                uid = uid,
                type = cardType,
                data = null,
                name = ""
            )
            NfcReadResult.Success(card)
        }
    }

    private fun detectCardType(tag: Tag): CardType {
        val techList = tag.techList
        return when {
            techList.contains("android.nfc.tech.MifareClassic") -> {
                val classic = MifareClassic.get(tag)
                if (classic != null) {
                    try {
                        val size = classic.size
                        classic.close()
                        if (size == MifareClassic.SIZE_1K) CardType.MIFARE_CLASSIC_1K
                        else CardType.MIFARE_CLASSIC_4K
                    } catch (e: Exception) {
                        CardType.UNKNOWN
                    }
                } else CardType.UNKNOWN
            }
            techList.contains("android.nfc.tech.MifareUltralight") -> CardType.MIFARE_ULTRALIGHT
            techList.contains("android.nfc.tech.NfcA") -> CardType.UNKNOWN
            else -> CardType.UNKNOWN
        }
    }

    private fun readMifareClassic(tag: Tag): ByteArray? {
        var classic: MifareClassic? = null
        return try {
            classic = MifareClassic.get(tag)
            classic.connect()

            val blockCount = classic.blockCount
            val totalBytes = blockCount * MifareClassic.BLOCK_SIZE

            ByteArray(totalBytes).also { buffer ->
                var offset = 0
                val sectorCount = classic.sectorCount
                for (sector in 0 until sectorCount) {
                    val sectorIndex = classic.sectorToBlock(sector)
                    val blockCountInSector = if (sector < 4) 4 else 5
                    for (block in 0 until blockCountInSector) {
                        try {
                            val blockData = classic.readBlock(sectorIndex + block)
                            if (blockData != null) {
                                blockData.copyInto(buffer, offset)
                                offset += blockData.size
                            }
                        } catch (e: Exception) {
                            // Skip unreadable blocks
                        }
                    }
                }
            }
        } catch (e: Exception) {
            null
        } finally {
            try { classic?.close() } catch (e: Exception) { }
        }
    }

    private fun readMifareUltralight(tag: Tag): ByteArray? {
        var ultralight: MifareUltralight? = null
        return try {
            ultralight = MifareUltralight.get(tag)
            ultralight.connect()

            val type = ultralight.type
            val pageCount = if (type == MifareUltralight.TYPE_ULTRALIGHT_C) 48 else 16

            val buffer = ByteArray(pageCount * 4)
            for (page in 0 until pageCount) {
                try {
                    val pageData = ultralight.readPages(page)
                    if (pageData != null) {
                        pageData.copyInto(buffer, page * 4, 0, minOf(pageData.size, 4))
                    }
                } catch (e: Exception) {
                    // Skip unreadable pages
                }
            }
            buffer
        } catch (e: Exception) {
            null
        } finally {
            try { ultralight?.close() } catch (e: Exception) { }
        }
    }

}