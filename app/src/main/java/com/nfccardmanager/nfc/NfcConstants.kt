package com.nfccardmanager.nfc

object NfcConstants {
    const val ACTION_TAG_DISCOVERED = "android.nfc.action.TECH_DISCOVERED"
    const val ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED"
    const val ACTION_TAG_LOST = "android.nfc.action.TAG_LOST"

    const val EXTRA_TAG = "android.nfc.extra.TAG"
    const val EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES"
    const val EXTRA_ID = "android.nfc.extra.ID"

    const val AID = "F0434F464F43415244"

    val SELECT_COMMAND = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00)
    val READ_COMMAND = byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x00)
}