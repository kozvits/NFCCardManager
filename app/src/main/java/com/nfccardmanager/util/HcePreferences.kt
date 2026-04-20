package com.nfccardmanager.util

import android.content.Context
import android.content.SharedPreferences
import com.nfccardmanager.nfc.NfcConstants

class HcePreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    fun setUid(uid: String) {
        prefs.edit().putString(KEY_UID, uid).apply()
    }

    fun getUid(): String? {
        return prefs.getString(KEY_UID, null)
    }

    fun setAid(aid: String) {
        prefs.edit().putString(KEY_AID, aid).apply()
    }

    fun getAid(): String {
        return prefs.getString(KEY_AID, NfcConstants.AID) ?: NfcConstants.AID
    }

    fun setCardData(data: ByteArray?) {
        if (data != null) {
            prefs.edit().putString(KEY_CARD_DATA, data.toHexString()).apply()
        } else {
            prefs.edit().remove(KEY_CARD_DATA).apply()
        }
    }

    fun getCardData(): ByteArray? {
        val hex = prefs.getString(KEY_CARD_DATA, null) ?: return null
        return try {
            hex.hexToBytes()
        } catch (e: Exception) {
            null
        }
    }

    fun setCardType(type: String) {
        prefs.edit().putString(KEY_CARD_TYPE, type).apply()
    }

    fun getCardType(): String? {
        return prefs.getString(KEY_CARD_TYPE, null)
    }

    fun setEmulationActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_EMULATION_ACTIVE, active).apply()
    }

    fun isEmulationActive(): Boolean {
        return prefs.getBoolean(KEY_EMULATION_ACTIVE, false)
    }

    fun clearEmulation() {
        prefs.edit()
            .remove(KEY_UID)
            .remove(KEY_CARD_DATA)
            .remove(KEY_EMULATION_ACTIVE)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "hce_prefs"
        private const val KEY_UID = "uid"
        private const val KEY_AID = "aid"
        private const val KEY_CARD_DATA = "card_data"
        private const val KEY_CARD_TYPE = "card_type"
        private const val KEY_EMULATION_ACTIVE = "emulation_active"
    }
}