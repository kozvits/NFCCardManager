package com.nfccardmanager.service

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import com.nfccardmanager.nfc.NfcConstants
import com.nfccardmanager.util.HcePreferences
import com.nfccardmanager.util.hexToBytes
import com.nfccardmanager.util.toHexString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CardEmulationService : HostApduService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var hcePreferences: HcePreferences? = null

    override fun onCreate() {
        super.onCreate()
        hcePreferences = HcePreferences(this)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return createErrorResponse()
        }

        val commandString = commandApdu.toHexString()

        // Select AID command
        if (commandApdu.size >= 4 && commandApdu[0] == 0x00.toByte() &&
            commandApdu[1] == 0xA4.toByte() && commandApdu[2] == 0x04.toByte()) {
            return handleSelectAid(commandApdu)
        }

        // Read command (typically READ)
        if (commandApdu.size >= 4 && commandApdu[0] == 0x00.toByte() &&
            (commandApdu[1] == 0xB0.toByte() || commandApdu[1] == 0xBD.toByte() ||
             commandApdu[1] == 0xB1.toByte())) {
            return handleReadCommand(commandApdu)
        }

        // Default response - return UID or stored data
        return getEmulatedResponse()
    }

    private fun handleSelectAid(commandApdu: ByteArray): ByteArray {
        val aid = hcePreferences?.getAid() ?: NfcConstants.AID
        val expectedSelectCommand = NfcConstants.SELECT_COMMAND + aid.length.toByte() + aid.hexToBytes()

        if (commandApdu.contentEquals(expectedSelectCommand)) {
            return createSuccessResponse()
        }

        return createErrorResponse()
    }

    private fun handleReadCommand(commandApdu: ByteArray): ByteArray {
        return getEmulatedResponse()
    }

    private fun getEmulatedResponse(): ByteArray {
        val uid = hcePreferences?.getUid() ?: return createErrorResponse()
        val cardData = hcePreferences?.getCardData()

        return if (cardData != null) {
            createDataResponse(cardData)
        } else {
            createUidResponse(uid.hexToBytes())
        }
    }

    private fun createSuccessResponse(): ByteArray {
        return byteArrayOf(0x90.toByte(), 0x00.toByte())
    }

    private fun createErrorResponse(): ByteArray {
        return byteArrayOf(0x6F.toByte(), 0x00.toByte())
    }

    private fun createUidResponse(uid: ByteArray): ByteArray {
        val response = ByteArray(uid.size + 2)
        uid.copyInto(response, 0)
        response[uid.size] = 0x90.toByte()
        response[uid.size + 1] = 0x00.toByte()
        return response
    }

    private fun createDataResponse(data: ByteArray): ByteArray {
        val maxLength = 255
        val responseData = data.copyOf(minOf(data.size, maxLength))
        val response = ByteArray(responseData.size + 2)
        responseData.copyInto(response, 0)
        response[responseData.size] = 0x90.toByte()
        response[responseData.size + 1] = 0x00.toByte()
        return response
    }

    override fun onDeactivated(reason: Int) {
        if (reason == DEACTIVATION_LINK_LOSS) {
            hcePreferences?.clearEmulation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}