package de.thoska.pulse

import de.thoska.pulse.reader.DesfireApduParser
import de.thoska.pulse.reader.DesfireReader
import de.thoska.pulse.reader.IsoDepTransceiver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesfireReaderTest {

    private class MockIsoDepTransceiver(
        private val mockUid: ByteArray = byteArrayOf(0x04, 0x12, 0x34, 0x56, 0x78, 0x9A.toByte(), 0xBC.toByte()),
        private val commandResponses: Map<String, ByteArray> = emptyMap()
    ) : IsoDepTransceiver {
        val transceivedCommands = mutableListOf<String>()

        override suspend fun transceive(command: ByteArray): ByteArray {
            val hex = DesfireApduParser.toSpacedHex(command)
            transceivedCommands.add(hex)
            return commandResponses[hex] ?: byteArrayOf(0x91.toByte(), 0x00)
        }

        override fun getUid(): ByteArray = mockUid
    }

    @Test
    fun `test full successful card read with primary AID`() = runBlocking {
        // Mock responses:
        // 1. Select AID 5F 84 15 -> 91 00
        // 2. Read Balance -> 14,50 EUR = [0xA4, 0x38, 0x00, 0x00, 0x91, 0x00]
        // 3. Read Settings -> 3,20 EUR meal at offset 12..15
        val settings = ByteArray(19)
        settings[0] = 0x03
        settings[12] = 0x80.toByte()
        settings[13] = 0x0C.toByte()
        settings[17] = 0x91.toByte()
        settings[18] = 0x00

        val responses = mapOf(
            "90 5A 00 00 03 5F 84 15 00" to byteArrayOf(0x91.toByte(), 0x00),
            "90 6C 00 00 01 01 00" to byteArrayOf(0xA4.toByte(), 0x38.toByte(), 0x00, 0x00, 0x91.toByte(), 0x00),
            "90 F5 00 00 01 01 00" to settings
        )

        val transceiver = MockIsoDepTransceiver(commandResponses = responses)
        val reader = DesfireReader(transceiver)

        val result = reader.readCard()
        assertTrue(result.isSuccess)

        val card = result.getOrThrow()
        assertEquals("04:12:34:56:78:9A:BC", card.cardUid)
        assertEquals(14.50, card.balanceEur, 0.001)
        assertEquals(3.20, card.lastTransactionEur, 0.001)
        assertEquals("5F8415", card.selectedAid)
        assertEquals(3, card.apduLogs.size)
    }

    @Test
    fun `test card read fallback when primary AID fails`() = runBlocking {
        val settings = ByteArray(19)
        settings[12] = 0x50.toByte()
        settings[13] = 0x0A.toByte() // 2640 = 2.64 EUR
        settings[17] = 0x91.toByte()
        settings[18] = 0x00

        val responses = mapOf(
            // Primary AID fails with 91 1C (File/App not found)
            "90 5A 00 00 03 5F 84 15 00" to byteArrayOf(0x91.toByte(), 0x1C.toByte()),
            // Fallback AID succeeds with 91 00
            "90 5A 00 00 03 15 84 5F 00" to byteArrayOf(0x91.toByte(), 0x00),
            // Read Balance -> 8,50 EUR = 8500 = 0x00002134 -> [0x34, 0x21, 0x00, 0x00]
            "90 6C 00 00 01 01 00" to byteArrayOf(0x34.toByte(), 0x21.toByte(), 0x00, 0x00, 0x91.toByte(), 0x00),
            "90 F5 00 00 01 01 00" to settings
        )

        val transceiver = MockIsoDepTransceiver(commandResponses = responses)
        val reader = DesfireReader(transceiver)

        val result = reader.readCard()
        assertTrue(result.isSuccess)

        val card = result.getOrThrow()
        assertEquals("15845F", card.selectedAid)
        assertEquals(8.50, card.balanceEur, 0.001)
        assertEquals(2.64, card.lastTransactionEur, 0.001)
        assertEquals(4, card.apduLogs.size)
    }

    @Test
    fun `test card read fails when both primary and fallback AID are rejected`() = runBlocking {
        val responses = mapOf(
            "90 5A 00 00 03 5F 84 15 00" to byteArrayOf(0x91.toByte(), 0x1C.toByte()),
            "90 5A 00 00 03 15 84 5F 00" to byteArrayOf(0x91.toByte(), 0x1C.toByte())
        )

        val transceiver = MockIsoDepTransceiver(commandResponses = responses)
        val reader = DesfireReader(transceiver)

        val result = reader.readCard()
        assertTrue(result.isFailure)
    }
}
