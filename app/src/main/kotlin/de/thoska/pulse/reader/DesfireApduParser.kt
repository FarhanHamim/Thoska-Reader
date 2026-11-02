package de.thoska.pulse.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Pure APDU builder and response parser for MIFARE DESFire EV1/EV2/EV3 cards
 * using ISO 7816-4 wrapped framing.
 */
object DesfireApduParser {

    /**
     * ISO 7816-4 wrapped command to Select Application (DESFire INS 0x5A).
     * AID: 0x5F8415 (Thoska University Mensa Application ID)
     * APDU: 90 5A 00 00 03 5F 84 15 00
     */
    val SELECT_AID_PRIMARY: ByteArray = byteArrayOf(
        0x90.toByte(), 0x5A.toByte(), 0x00, 0x00, 0x03,
        0x5F.toByte(), 0x84.toByte(), 0x15,
        0x00
    )

    /**
     * Fallback Select AID with reversed byte order (0x15845F) for compatibility
     * with alternate DESFire AID endianness conventions on older cards.
     * APDU: 90 5A 00 00 03 15 84 5F 00
     */
    val SELECT_AID_FALLBACK: ByteArray = byteArrayOf(
        0x90.toByte(), 0x5A.toByte(), 0x00, 0x00, 0x03,
        0x15, 0x84.toByte(), 0x5F.toByte(),
        0x00
    )

    /**
     * ISO 7816-4 wrapped command to Get Value / Read Balance (DESFire INS 0x6C)
     * for Value File 0x01.
     * APDU: 90 6C 00 00 01 01 00
     */
    val READ_BALANCE_FILE_01: ByteArray = byteArrayOf(
        0x90.toByte(), 0x6C.toByte(), 0x00, 0x00, 0x01,
        0x01,
        0x00
    )

    /**
     * ISO 7816-4 wrapped command to Get File Settings (DESFire INS 0xF5)
     * for File 0x01.
     * APDU: 90 F5 00 00 01 01 00
     */
    val READ_SETTINGS_FILE_01: ByteArray = byteArrayOf(
        0x90.toByte(), 0xF5.toByte(), 0x00, 0x00, 0x01,
        0x01,
        0x00
    )

    /**
     * Checks if the status bytes at the end of the response indicate success.
     * In ISO 7816-4 wrapped DESFire:
     * - SW1 = 0x91, SW2 = 0x00 (DESFire success: 0x00 OPERATION_OK wrapped in 0x91)
     * - SW1 = 0x90, SW2 = 0x00 (Standard ISO 7816 success)
     */
    fun isSuccessResponse(response: ByteArray): Boolean {
        if (response.size < 2) return false
        val sw1 = response[response.size - 2]
        val sw2 = response[response.size - 1]
        return isSuccessStatus(sw1, sw2)
    }

    fun isSuccessStatus(sw1: Byte, sw2: Byte): Boolean {
        return (sw1 == 0x91.toByte() && sw2 == 0x00.toByte()) ||
                (sw1 == 0x90.toByte() && sw2 == 0x00.toByte())
    }

    /**
     * Extracts status word as a 4-character hex string (e.g. "9100" or "917E").
     */
    fun extractStatusWord(response: ByteArray): String {
        if (response.size < 2) return "UNKNOWN"
        val sw1 = response[response.size - 2].toInt() and 0xFF
        val sw2 = response[response.size - 1].toInt() and 0xFF
        return String.format("%02X%02X", sw1, sw2)
    }

    /**
     * Parse 4-byte little-endian integer balance from File 0x01 response.
     *
     * Expected input: at least 4 bytes of value data. If the response contains
     * trailing status bytes (e.g., 6 bytes total [D0, 07, 00, 00, 91, 00]),
     * the status word is verified and the first 4 bytes are parsed.
     *
     * Value calculation: raw Little-Endian 32-bit int divided by 1000.0.
     */
    fun parseBalanceEur(response: ByteArray): Double {
        if (response.size < 4) {
            throw IllegalArgumentException("Balance response too short: ${response.size} bytes (minimum 4 bytes required)")
        }

        // If response includes 2-byte status word at the end, verify it
        if (response.size >= 6) {
            val sw1 = response[response.size - 2]
            val sw2 = response[response.size - 1]
            if (!isSuccessStatus(sw1, sw2)) {
                throw IllegalStateException("Card returned error status ${extractStatusWord(response)} when reading balance")
            }
        }

        val rawInt = ByteBuffer.wrap(response, 0, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int

        return rawInt / 1000.0
    }

    /**
     * Parse last transaction amount from File 0x01 Settings response (File Type 0x03 Value File).
     *
     * Per DESFire specifications and requirement:
     * Bytes 12..15 contain the 4-byte little-endian limited credit / last transaction delta.
     *
     * Value calculation: raw Little-Endian 32-bit int divided by 1000.0.
     */
    fun parseLastTransactionEur(response: ByteArray): Double {
        if (response.size < 16) {
            throw IllegalArgumentException("File settings response too short: ${response.size} bytes (minimum 16 bytes required to read bytes 12..15)")
        }

        // If response includes 2-byte status word at the end, verify it
        if (response.size >= 18) {
            val sw1 = response[response.size - 2]
            val sw2 = response[response.size - 1]
            if (!isSuccessStatus(sw1, sw2)) {
                throw IllegalStateException("Card returned error status ${extractStatusWord(response)} when reading file settings")
            }
        }

        val rawInt = ByteBuffer.wrap(response, 12, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int

        return rawInt / 1000.0
    }

    /**
     * Convert ByteArray to spaced uppercase hex string (e.g. "90 5A 00 00 03 5F 84 15 00").
     */
    fun toSpacedHex(bytes: ByteArray): String {
        return bytes.joinToString(" ") { String.format("%02X", it.toInt() and 0xFF) }
    }

    /**
     * Convert ByteArray to colon-delimited hex string (e.g. "04:A2:3B:4C").
     */
    fun toColonHex(bytes: ByteArray): String {
        return bytes.joinToString(":") { String.format("%02X", it.toInt() and 0xFF) }
    }

    /**
     * Parse hex string (with or without spaces or colons) into ByteArray.
     */
    fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace(" ", "").replace(":", "")
        require(clean.length % 2 == 0) { "Invalid hex string length: ${clean.length}" }
        val result = ByteArray(clean.length / 2)
        for (i in clean.indices step 2) {
            result[i / 2] = clean.substring(i, i + 2).toInt(16).toByte()
        }
        return result
    }
}
