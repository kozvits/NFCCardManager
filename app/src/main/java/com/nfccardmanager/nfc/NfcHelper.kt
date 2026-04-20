package com.nfccardmanager.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var nfcAdapter: NfcAdapter? = null

    init {
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        } catch (e: Exception) {
            nfcAdapter = null
        }
    }

    fun isNfcAvailable(): Boolean = nfcAdapter != null

    fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true

    fun enableForegroundDispatch(activity: Activity) {
        try {
            val adapter = nfcAdapter ?: return
            if (!adapter.isEnabled) return

            val intent = Intent(activity, activity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getActivity(activity, 0, intent, flags)
            
            adapter.enableForegroundDispatch(activity, pendingIntent, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disableForegroundDispatch(activity: Activity) {
        try {
            nfcAdapter?.disableForegroundDispatch(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resolveIntent(intent: Intent?): Tag? {
        if (intent == null) return null

        val action = intent.action
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
        }
        return null
    }

    fun getAdapter(): NfcAdapter? = nfcAdapter
}
