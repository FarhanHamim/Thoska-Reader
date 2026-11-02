package de.thoska.pulse.reader

import android.nfc.tech.IsoDep

/**
 * Android hardware implementation of [IsoDepTransceiver] wrapping [android.nfc.tech.IsoDep].
 * Connects and configures standard timeouts for DESFire APDUs.
 */
class AndroidIsoDepTransceiver(
    private val isoDep: IsoDep
) : IsoDepTransceiver {

    init {
        // Set an adequate timeout for DESFire crypto and flash read operations
        try {
            isoDep.timeout = 5000
        } catch (_: Exception) {}
    }

    override suspend fun transceive(command: ByteArray): ByteArray {
        if (!isoDep.isConnected) {
            isoDep.connect()
        }
        return isoDep.transceive(command)
    }

    override fun getUid(): ByteArray? {
        return try {
            isoDep.tag.id
        } catch (_: Exception) {
            null
        }
    }
}
