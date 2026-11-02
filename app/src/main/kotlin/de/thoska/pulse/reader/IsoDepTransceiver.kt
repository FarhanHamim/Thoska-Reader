package de.thoska.pulse.reader

/**
 * Abstraction over raw ISO-DEP (ISO 14443-4 Type A) transceiving.
 * Allows decoupling card protocol logic from Android NfcAdapter / IsoDep hardware,
 * enabling deterministic unit testing with mock transceivers.
 */
interface IsoDepTransceiver {
    suspend fun transceive(command: ByteArray): ByteArray
    fun getUid(): ByteArray? = null
}
