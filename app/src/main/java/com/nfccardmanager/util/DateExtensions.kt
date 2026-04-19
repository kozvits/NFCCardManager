package com.nfccardmanager.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Extensions {

    fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }

    fun String.hexToBytes(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    fun Long.toDateString(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(this))
    }

    fun ByteArray.toReadableString(): String {
        return joinToString(" ") { "%02X".format(it) }
    }
}