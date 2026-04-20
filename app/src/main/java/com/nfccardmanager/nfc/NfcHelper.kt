package com.nfccardmanager.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import com.nfccardmanager.presentation.ui.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var activityClass: Class<*>? = null

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    }

    fun setupPendingIntent(cls: Class<*>) {
        activityClass = cls
        val intent = Intent(context, cls).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
    }

    fun isNfcAvailable(): Boolean = nfcAdapter != null

    fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true

    fun enableForegroundDispatch(activity: Activity) {
        val adapter = nfcAdapter ?: return
        if (!adapter.isEnabled) return

        if (pendingIntent == null || activityClass == null) {
            setupPendingIntent(MainActivity::class.java)
        }

        try {
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
